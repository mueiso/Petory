// package com.study.petory.common.security;
//
// import static org.mockito.Mockito.*;
//
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// import org.springframework.data.redis.core.RedisTemplate;
// import org.springframework.mock.web.MockHttpServletRequest;
// import org.springframework.mock.web.MockHttpServletResponse;
// import org.springframework.security.core.context.SecurityContextHolder;
//
// import io.jsonwebtoken.Claims;
// import io.jsonwebtoken.Jwts;
// import jakarta.servlet.FilterChain;
//
// @WebMvcTest
// class JwtFilterTest {
//
// 	@InjectMocks
// 	private JwtFilter jwtFilter;
//
// 	@Mock
// 	private JwtProvider jwtProvider;
//
// 	@Mock
// 	private RedisTemplate<String, String> redisTemplate;
//
// 	private MockHttpServletRequest request;
// 	private MockHttpServletResponse response;
// 	private FilterChain filterChain;
//
// 	@BeforeEach
// 	void setUp() {
// 		request = new MockHttpServletRequest();
// 		response = new MockHttpServletResponse();
// 		filterChain = mock(FilterChain.class);
// 	}
//
// 	@Test
// 	void doFilter_ValidToken_SetsAuthentication() throws Exception {
// 		String token = "Bearer validToken";
//
// 		request.addHeader("Authorization", token);
//
// 		Claims claims = Jwts.claims().setSubject("1");
// 		claims.put("email", "user@example.com");
// 		claims.put("nickname", "nickname");
//
// 		when(jwtProvider.resolveToken(any())).thenReturn("validToken");
// 		when(jwtProvider.getClaims(any())).thenReturn(claims);
// 		when(redisTemplate.hasKey("logout:validToken")).thenReturn(false);
//
// 		jwtFilter.doFilter(request, response, filterChain);
//
// 		assertNotNull(SecurityContextHolder.getContext().getAuthentication());
// 		verify(filterChain).doFilter(request, response);
// 	}
//
// 	@Test
// 	void doFilter_BlacklistedToken_ShouldNotAuthenticate() throws Exception {
// 		String token = "Bearer blacklisted";
//
// 		request.addHeader("Authorization", token);
//
// 		when(jwtProvider.resolveToken(any())).thenReturn("blacklisted");
// 		when(redisTemplate.hasKey("logout:blacklisted")).thenReturn(true);
//
// 		jwtFilter.doFilter(request, response, filterChain);
//
// 		assertNull(SecurityContextHolder.getContext().getAuthentication());
// 	}
// }
