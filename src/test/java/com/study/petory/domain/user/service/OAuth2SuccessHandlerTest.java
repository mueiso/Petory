package com.study.petory.domain.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;

import com.study.petory.domain.user.dto.TokenResponseDto;
import com.study.petory.domain.user.entity.User;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
class OAuth2SuccessHandlerTest {

	@Mock
	private AuthService authService;

	@InjectMocks
	private OAuth2SuccessHandler oAuth2SuccessHandler;

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	@Test
	void onAuthenticationSuccess_정상동작_테스트() throws Exception {
		// given
		Map<String, Object> attributes = new HashMap<>();
		attributes.put("email", "user@example.com");
		attributes.put("name", "nickname");

		// 실제 OAuth2User 구현체 사용
		OAuth2User oauth2User = new DefaultOAuth2User(
			java.util.List.of(new OAuth2UserAuthority(attributes)),
			attributes,
			"email"
		);

		UsernamePasswordAuthenticationToken authentication =
			new UsernamePasswordAuthenticationToken(oauth2User, null, oauth2User.getAuthorities());

		TokenResponseDto tokens = new TokenResponseDto("accessTokenValue", "refreshTokenValue");
		when(authService.issueToken(any(User.class))).thenReturn(tokens);

		// response mock: 쿠키, sendRedirect 체크
		doNothing().when(response).addCookie(any(Cookie.class));
		doNothing().when(response).sendRedirect(anyString());

		// when
		oAuth2SuccessHandler.onAuthenticationSuccess(request, response, authentication);

		// then: 토큰 발급 및 쿠키 등록, 리다이렉트 동작 여부 검증
		verify(authService).issueToken(any(User.class));
		verify(response).addCookie(argThat(cookie -> {
			assertEquals("refreshToken", cookie.getName());
			assertTrue(cookie.isHttpOnly());
			assertTrue(cookie.getSecure());
			assertEquals("/", cookie.getPath());
			assertTrue(cookie.getMaxAge() > 0);
			return true;
		}));
		verify(response).sendRedirect(contains("accessToken=accessTokenValue"));
	}
}
