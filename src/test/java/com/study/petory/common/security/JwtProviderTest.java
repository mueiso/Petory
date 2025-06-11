package com.study.petory.common.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Base64;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import io.jsonwebtoken.Claims;

@ExtendWith(MockitoExtension.class)
class JwtProviderTest {

	@InjectMocks
	private JwtProvider jwtProvider;

	@Mock
	private RedisTemplate<String, String> redisTemplate;

	@Value("${jwt.secret.key}")
	private String secretKey = Base64.getEncoder().encodeToString("testSecretKeytestSecretKeytestSecretKey".getBytes());

	@BeforeEach
	void setUp() {
		ReflectionTestUtils.setField(jwtProvider, "secretKey", secretKey);
		jwtProvider.init(); // @PostConstruct 수동 호출
	}

	@Test
	void createAndParseAccessToken_success() {
		String token = jwtProvider.createAccessToken(1L, "user@example.com", "nickname");
		Claims claims = jwtProvider.getClaims(token);

		assertEquals("1", claims.getSubject());
		assertEquals("user@example.com", claims.get("email"));
		assertEquals("nickname", claims.get("nickname"));
	}

	// @Test
	// void getClaims_expiredToken_throwsException() {
	// 	// 직접 만료된 토큰 생성
	// 	String expiredToken = Jwts.builder()
	// 		.setSubject("1")
	// 		.setExpiration(new Date(System.currentTimeMillis() - 1000))
	// 		.signWith(jwtProvider.getKey(), SignatureAlgorithm.HS256)
	// 		.compact();
	//
	// 	String bearerToken = "Bearer " + expiredToken;
	//
	// 	CustomException exception = assertThrows(CustomException.class, () ->
	// 		jwtProvider.getClaims(bearerToken)
	// 	);
	// 	assertEquals(ErrorCode.EXPIRED_TOKEN, exception.getErrorCode());
	// }

	@Test
	void isValidRefreshToken_matchInRedis() {
		String email = "user@example.com";
		String refreshToken = "Bearer mockToken";

		ValueOperations<String, String> ops = Mockito.mock(ValueOperations.class);
		when(redisTemplate.opsForValue()).thenReturn(ops);
		when(ops.get(email)).thenReturn(refreshToken);

		boolean result = jwtProvider.isValidRefreshToken(email, refreshToken);
		assertTrue(result);
	}
}
