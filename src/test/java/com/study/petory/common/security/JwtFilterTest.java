package com.study.petory.common.security;

import static org.mockito.Mockito.*;

import java.io.PrintWriter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

class JwtFilterTest {

	@Mock
	private JwtProvider jwtProvider;

	@Mock
	private RedisTemplate<String, String> redisTemplate;

	@InjectMocks
	private JwtFilter jwtFilter;

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	@Mock
	private FilterChain filterChain;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		jwtFilter = new JwtFilter(jwtProvider, redisTemplate);
	}

	@Test
	void doFilterInternal_WHITELIST_패스() throws Exception {
		// given
		when(request.getRequestURI()).thenReturn("/auth/login");

		// when
		jwtFilter.doFilterInternal(request, response, filterChain);

		// then
		verify(filterChain).doFilter(request, response);
		verifyNoInteractions(jwtProvider);
	}

	@Test
	void doFilterInternal_Authorization_헤더_없으면_401() throws Exception {
		// given
		when(request.getRequestURI()).thenReturn("/api/protected");
		when(request.getHeader("Authorization")).thenReturn(null);
		// response.getWriter()를 mock 처리
		PrintWriter writer = mock(PrintWriter.class);
		when(response.getWriter()).thenReturn(writer);

		// when
		jwtFilter.doFilterInternal(request, response, filterChain);

		// then
		verify(response).setStatus(HttpStatus.UNAUTHORIZED.value());
		verify(writer).write(contains("Authorization 헤더가 존재하지 않습니다."));
		verifyNoInteractions(jwtProvider);
	}

	@Test
	void doFilterInternal_블랙리스트된_토큰이면_401() throws Exception {
		// given
		String bearerJwt = "Bearer validToken";
		when(request.getRequestURI()).thenReturn("/api/protected");
		when(request.getHeader("Authorization")).thenReturn(bearerJwt);

		// 정상적인 JWT 파싱
		when(jwtProvider.subStringToken(bearerJwt)).thenReturn("validToken");
		when(redisTemplate.hasKey("BLACKLIST_validToken")).thenReturn(true);

		PrintWriter writer = mock(PrintWriter.class);
		when(response.getWriter()).thenReturn(writer);

		// when
		jwtFilter.doFilterInternal(request, response, filterChain);

		// then
		verify(response).setStatus(HttpStatus.UNAUTHORIZED.value());
		verify(writer).write(contains("로그아웃된 토큰입니다."));
		verifyNoInteractions(filterChain);
	}

	@Test
	void doFilterInternal_정상_토큰이면_인증성공() throws Exception {
		// given
		String bearerJwt = "Bearer validToken";
		when(request.getRequestURI()).thenReturn("/api/protected");
		when(request.getHeader("Authorization")).thenReturn(bearerJwt);
		when(jwtProvider.subStringToken(bearerJwt)).thenReturn("validToken");
		when(redisTemplate.hasKey("BLACKLIST_validToken")).thenReturn(false);

		// Claims mock (유저 정보 포함)
		Claims claims = mock(Claims.class);
		when(jwtProvider.parseRawToken("validToken")).thenReturn(claims);
		when(claims.getSubject()).thenReturn("1");
		when(claims.get("email", String.class)).thenReturn("user@example.com");
		when(claims.get("nickname", String.class)).thenReturn("nickname");

		// when
		jwtFilter.doFilterInternal(request, response, filterChain);

		// then
		verify(filterChain).doFilter(request, response);
	}
}
