package com.study.petory.common.security;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.study.petory.common.exception.CustomException;
import com.study.petory.common.exception.enums.ErrorCode;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;

@Component
public class JwtProvider {

	private final RedisTemplate<String, String> loginRefreshToken;

	public JwtProvider(
		@Qualifier("loginRefreshToken")
		RedisTemplate<String, String> loginRefreshToken) {

		this.loginRefreshToken = loginRefreshToken;
	}

	/*
	 * 이 토큰을 가진 자 (=Bearer)가 인증된 사용자라는 의미의 표준 헤더 형식
	 * JWT 앞에 붙여서 인증 토큰임을 명시
	 * JWT 생성 시 붙임
	 * 검증 시 토큰을 파싱하기 전에 "Bearer " 를 제거하고 순수 JWT 문자열만 추출
	 */
	private static final String prefix = "Bearer ";

	/* TODO - 배포 전 accessTokenLife 15분으로 다시 수정
	 * JWT 토큰의 유효 시간 설정하는 상수
	 * 15분 * 60초 * 1000밀리초 = 900,000밀리초 = 15분
	 * 7일 * 24시간 * 60분 * 60초 * 1000밀리초 = 604,800,000밀리초 = 7일
	 */
	private static final long accessTokenLife = 30 * 1000L;  // 30초
	private static final long refreshTokenLife = 7 * 24 * 60 * 60 * 1000L;  // 7일

	/*
	 * @Value : 외부 설정값 주입받기 위해 사용
	 * application.properties 에서 JWT 서명에 사용할 시크릿 키를 주입받고, Key 객체로 변환해서 JWT 에 서명 시 사용
	 * Key : JWT 를 서명(Sign) 하거나 파싱(Verify) 할 때 사용되는 암호화 키 → @PostConstruct 메서드에서 초기화된다
	 */
	@Value("${jwt.secret.key}")
	private String secretKey;
	private Key key;

	/*
	 * @PostConstruct : Bean 생성 후 자동으로 호출되는 초기화 작업 메서드
	   → JWT 서명을 위한 암호화 키(Key)를 초기화하는 과정
	   → @Value 로 주입된 secretKey 를 가공해 Key 객체를 만들기 위해 사용
	 * 시크릿 키를 Base64 디코딩해 HMAC SHA 서명을 위한 Key 객체로 초기화
	 * 토큰 생성 및 검증 시 동일한 키로 서명/검증
	 */
	@PostConstruct
	public void init() {

		byte[] keyBytes = Decoders.BASE64.decode(secretKey);
		this.key = Keys.hmacShaKeyFor(keyBytes);
	}

	/*
	 * 주어진 사용자 정보(userId, email, nickname)를 JWT 의 Claim 으로 포함해서 Access Token 생성하는 메서드
	 * 리턴값 = 최종 생성된 JWT 문자열 (앞에 Bearer 포함)
	 * Date : 현재 시각 기준으로 Date 객체 생성 → issuedAt(토큰 발행 시점)과 expiration(만료 시점) 계산에 사용
	 */
	public String createAccessToken(Long userId, String email, String nickname, List<String> roles) {

		Date date = new Date();

		/*
		 * return prefix + Jwts.builder() : JJWT 에서 제공하는 생성 빌더 시작점 (prefix = "Bearer ")
		 * .setSubject : sub(기본 Claim 중 하나)에 userId 저장 → 고유 ID 저장해서 토큰만 보고도 어떤 유저인지 알 수 있도록
		 * .claim : 커스텀 Claim 추가 → JWT 에 사용자 이메일, 닉네임 같이 담아서 나중에 토큰만 디코딩해도 이 정보를 쉽게 꺼낼 수 있도록 한다
		 * .setExpiration : 토큰 만료 시간 설정 → 현재 시간 + tokenLife = exp(만료 시간)
		 * .setIssuedAt : 토큰 발급 시각을 현재 시작으로 설정
		 * .signWith : JWT 를 서명하는 부분 → 서명을 통해 토큰이 위조되지 않았는지 검증 가능
		 * .compact() : 위의 빌더 체인들을 실제 JWT 문자열로 변환
		 */
		return prefix + Jwts.builder()
			.setSubject(String.valueOf(userId))
			.claim("email", email)
			.claim("nickname", nickname)
			.claim("roles", roles)
			.setExpiration(new Date(date.getTime() + accessTokenLife))
			.setIssuedAt(date)
			.signWith(key, SignatureAlgorithm.HS256)
			.compact();
	}

	// 사용자 ID만 사용해 RefreshToken 생성 (보안성 위해 Claim 최소화)
	public String createRefreshToken(Long userId) {

		Date now = new Date();

		return prefix + Jwts.builder()
			.setSubject(String.valueOf(userId))
			.setExpiration(new Date(now.getTime() + refreshTokenLife))
			.setIssuedAt(now)
			.signWith(key, SignatureAlgorithm.HS256)
			.compact();
	}

	// "Bearer " 접두사가 있을 경우만 제거하고, 없으면 그대로 반환
	public String subStringToken(String token) {

		if (!StringUtils.hasText(token)) {
			throw new CustomException(ErrorCode.NO_TOKEN);
		}

		// 접두사가 있을 경우만 제거
		if (token.startsWith(prefix)) {
			return token.substring(prefix.length());
		}

		return token;
	}

	/*
	 * Jwts.parserBuilder() : JJWT 라이브러리에서 제공하는 JWT 파서 빌더 객체 생성
	   → parserBuilder() 는 .build() 전에 키 또는 파싱 설정을 지정할 수 있도록 해준다
	 * setSigningKey(key) : JWT 의 서명을 검증할 키를 설정
	   → 이 키는 @PostConstruct 에서 HMAC-SHA256 방식으로 생성된 Key 객체이다
	   → 서명된 토큰인지 확인하는 데 사용된다 (위조 방지)
	 * .build() : 최종 JwtParser 객체 생성
	   → 토큰 파싱할 준비 완료
	 * .parseClaimsJws(token) : 전달받은 JWT 문자열을 JWS(서명된 JWT)로 파싱
	   → 이 과정에서 이루어지는 것 : 토큰의 유효성 검증, 서명 검증, 만료 기간 검사 등
	 *
	 */
	public Claims getClaims(String token) {

		String pureToken = subStringToken(token);

		try {
			// 정상적인 토큰 처리
			return Jwts.parserBuilder()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(pureToken)
				.getBody();
			// 토큰 만료
		} catch (ExpiredJwtException e) {
			throw new CustomException(ErrorCode.EXPIRED_TOKEN);
			// 서명 불일치
		} catch (SignatureException e) {
			throw new CustomException(ErrorCode.WRONG_SIGNATURE);
			// 기타 JWT 오류 (Malformed, Unsupported 등)
		} catch (JwtException e) {
			throw new CustomException(ErrorCode.INVALID_TOKEN);
		}
	}

	/*
	 * JwtProvider 의 getEmailFromToken 메서드 전용 getClaims
	 * 이미 "Bearer " 접두사를 제거한 순수 JWT 문자열을 파싱하여 Claims 반환
	 */
	public Claims parseRawToken(String token) {

		try {
			// 정상적인 토큰 처리
			return Jwts.parserBuilder()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(token)
				.getBody();
			// 토큰 만료
		} catch (ExpiredJwtException e) {
			throw new CustomException(ErrorCode.EXPIRED_TOKEN);
			// 서명 불일치
		} catch (SignatureException e) {
			throw new CustomException(ErrorCode.WRONG_SIGNATURE);
			// 기타 JWT 오류 (Malformed, Unsupported 등)
		} catch (JwtException e) {
			throw new CustomException(ErrorCode.INVALID_TOKEN);
		}
	}

	/*
	 * Redis 에 userId를 키로 하여 Refresh Token 을 저장
	 * Redis TTL(Time-To-Live)은 7일 → 자동 만료
	 */
	public void storeRefreshToken(Long userId, String refreshToken) {

		if (refreshToken.startsWith("Bearer ")) {
			refreshToken = subStringToken(refreshToken);
		} else {
			return;
		}

		long expireMillis = refreshTokenLife;
		loginRefreshToken.opsForValue().set(
			String.valueOf(userId),
			refreshToken,
			expireMillis,
			TimeUnit.MILLISECONDS
		);
	}

	// 로그아웃 시 Redis 에서 해당 사용자의 Refresh Token 삭제
	public void deleteRefreshToken(Long userId) {

		loginRefreshToken.delete(String.valueOf(userId));
	}

	/*
	 * Redis 에 저장된 토큰과 전달된 토큰이 일치하는지 검사
	 * 유효하지 않거나 존재하지 않으면 false 반환
	 */
	public boolean isValidRefreshToken(Long userId, String refreshToken) {

		String saved = loginRefreshToken.opsForValue().get(String.valueOf(userId));
		return saved != null && saved.equals(refreshToken);
	}

	// AccessToken 만료 여부 확인 메서드
	public boolean isAccessTokenExpired(String token) {
		try {
			getClaims(token);
			return false; // 예외 없이 Claims 얻었으면 유효
		} catch (CustomException ex) {
			if (ex.getErrorCode() == ErrorCode.EXPIRED_TOKEN) {
				return true;
			}
			throw ex;  // 그 외 예외는 그대로 던짐
		}
	}

	// 이메일 추출 메서드
	public String getEmailFromToken(String token) {

		Claims claims = parseRawToken(token);
		return claims.get("email", String.class);
	}
}
