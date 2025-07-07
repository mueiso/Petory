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

	private static final String prefix = "Bearer ";

	@Value("${jwt.access-token-valid-time}")
	private long accessTokenLife;

	@Value("${jwt.refresh-token-valid-time}")
	private long refreshTokenLife;

	// .properties 에서 JWT 서명에 사용할 시크릿 키를 주입받고, Key 객체로 변환해서 JWT 에 서명 시 사용
	@Value("${jwt.secret.key}")
	private String secretKey;
	private Key key;

	// JWT 서명을 위한 암호화 키(Key)를 초기화하는 과정 → @Value 로 주입된 secretKey 를 가공해 Key 객체를 만들기 위해 사용
	@PostConstruct
	public void init() {

		byte[] keyBytes = Decoders.BASE64.decode(secretKey);
		this.key = Keys.hmacShaKeyFor(keyBytes);
	}

	// 주어진 사용자 정보(userId, email, nickname)를 JWT 의 Claim 으로 포함해서 Access Token 생성하는 메서드
	public String createAccessToken(Long userId, String email, String nickname, List<String> roles) {

		Date date = new Date();

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

	// 토큰의 유효성 검증, 서명 검증, 만료 기간 검사
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

	// 이미 "Bearer " 접두사를 제거한 순수 JWT 문자열을 파싱하여 Claims 반환 (JwtProvider 의 getEmailFromToken 메서드 전용 getClaims)
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

	// Redis 키 네이밍: "RT:{userId}"
	private String getRefreshTokenKey(Long userId) {
		return "RT:" + userId;
	}

	// Redis 에 userId를 키로 하여 Refresh Token 을 저장
	public void storeRefreshToken(Long userId, String refreshToken) {

		if (refreshToken.startsWith("Bearer ")) {
			refreshToken = subStringToken(refreshToken);
		} else {
			return;
		}

		// Redis TTL(Time-To-Live)은 7일 → 자동 만료
		long expireMillis = refreshTokenLife;
		loginRefreshToken.opsForValue().set(
			getRefreshTokenKey(userId),
			refreshToken,
			expireMillis,
			TimeUnit.MILLISECONDS
		);
	}

	// 로그아웃 시 Redis 에서 해당 사용자의 Refresh Token 삭제
	public void deleteRefreshToken(Long userId) {

		loginRefreshToken.delete(getRefreshTokenKey(userId));
	}

	// Redis 에 저장된 토큰과 전달된 토큰이 일치하는지 검사
	public boolean isValidRefreshToken(Long userId, String refreshToken) {

		String saved = loginRefreshToken.opsForValue().get(getRefreshTokenKey(userId));
		return saved != null && saved.equals(refreshToken);
	}

	// AccessToken 만료 여부 확인 메서드
	public boolean isAccessTokenExpired(String token) {
		try {
			getClaims(token);
			return false;  // 예외 없이 Claims 얻었으면 유효
		} catch (CustomException ex) {
			if (ex.getErrorCode() == ErrorCode.EXPIRED_TOKEN) {
				return true;
			}
			throw ex;  // 그 외 예외는 그대로 던짐
		}
	}

	// JWT 에서 roles 클레임을 추출하여 문자열 리스트로 반환
	public List<String> getRolesFromToken(String token) {
		Claims claims = getClaims(token);

		Object rolesObject = claims.get("roles");

		if (rolesObject instanceof List<?> rolesList) {
			return rolesList.stream()
				.map(Object::toString)
				.toList();  // ["ROLE_USER", "ROLE_ADMIN"]
		}

		return List.of();  // roles 클레임이 없거나 비어 있으면 빈 리스트
	}

	public JwtProvider(
		@Qualifier("loginRefreshToken")
		RedisTemplate<String, String> loginRefreshToken) {

		this.loginRefreshToken = loginRefreshToken;
	}
}
