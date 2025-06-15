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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
class OAuth2SuccessHandlerTest {

	@Mock
	private AuthServiceImpl authServiceImpl;

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

		OAuth2User oauth2User = new DefaultOAuth2User(
			java.util.List.of(new OAuth2UserAuthority(attributes)),
			attributes,
			"email"
		);

		UsernamePasswordAuthenticationToken authentication =
			new UsernamePasswordAuthenticationToken(oauth2User, null, oauth2User.getAuthorities());

		TokenResponseDto tokens = new TokenResponseDto("accessTokenValue", "refreshTokenValue");

		when(authServiceImpl.issueToken(any(User.class))).thenReturn(tokens);
		doNothing().when(response).setHeader(anyString(), anyString());
		doNothing().when(response).sendRedirect(anyString());

		// when
		oAuth2SuccessHandler.onAuthenticationSuccess(request, response, authentication);

		// then
		verify(authServiceImpl).issueToken(any(User.class));

		verify(response).setHeader(eq("Authorization-Refresh"), anyString());

		verify(response).sendRedirect(argThat(url ->
			url.contains("accessToken=accessTokenValue") &&
				url.contains("refreshToken=refreshTokenValue")
		));
	}
}
