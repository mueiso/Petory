package com.study.petory.common.auth;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class JwtUtil {

	/*
	 * 이 토큰을 가진 자 (=Bearer)가 인증된 사용자라는 의미의 표준 헤더 형식
	 * JWT 앞에 붙여서 인증 토큰임을 명시
	 * JWT 생성 시 붙임
	 * 검증 시 토큰을 파싱하기 전에 "Bearer " 를 제거하고 순수 JWT 문자열만 추출
	 */
	private static final String prefix = "Bearer ";

	/*
	 * JWT 토큰의 유효 시간 설정하는 상수
	 * 15분 * 60초 * 1000밀리초 = 900,000밀리초 = 15분
	 * 7일 * 24시간 * 60분 * 60초 * 1000밀리초 = 604,800,000밀리초 = 7일
	 */
	private static final long accessTokenLife = 15 * 60 * 1000L;  // 15분
	private static final long refreshTokenLife = 7 * 24 * 60 * 60 * 1000L;  // 7일

	/*
	 * @Value : 외부 설정값 주입받기 위해 사용
	 * ${jwt.secret.key} : application.properties 에 정의된 값을 가져온다
	 * Key : JWT 를 서명(Sign) 하거나 파싱(Verify) 할 때 사용되는 암호화 키 → @PostConstruct 메서드에서 초기화된다
	 */
	@Value("${jwt.secret.key}")
	private String secretKey;
	private Key key;

	/*
	* @PostConstruct : Bean 생성 후 자동으로 호출되는 초기화 작업 메서드
	  → JWT 서명을 위한 암호화 키(Key)를 초기화하는 과정
	  → @Value 로 주입된 secretKey 를 가공해 Key 객체를 만들기 위해 사용
	* Decoders.BASE64.decode : =Base64 로 인코딩된 문자열인 secretKey 를 byte 배열로 디코딩
	* this.key : 생성된 Key 를 클래스 필드에 저장해 나중에 사용
	* Keys.hmacShaKeyFor() : 전달된 byte 값을 기반으로 HMAC-SHA 용 SecretKey 객체를 생성
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
	* return prefix + Jwts.builder() : JJWT 에서 제공하는 생성 빌더 시작점 (prefix = "Bearer ")
	 */
	public String createToken(Long uerId, String email, String nickname) {

		Date date = new Date();

		return prefix + Jwts.builder()
			.setSubject(String.valueOf(userId))
			.claim("email", email)
			.claim("nickname", nickname)
			.setExpiration(new Date(date.getTime() + tokenLife))
			.setIssuedAt(date)
			.signWith(key, SignatureAlgorithm.HS256)
			.compact();
	}

}
