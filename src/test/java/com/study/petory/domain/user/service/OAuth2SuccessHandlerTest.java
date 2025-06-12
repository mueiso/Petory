package com.study.petory.domain.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.petory.common.security.JwtProvider;

import jakarta.servlet.http.HttpServletResponse;

@WebMvcTest
class OAuth2SuccessHandlerTest {

	@InjectMocks
	private OAuth2SuccessHandler successHandler;

	@Mock
	private JwtProvider jwtProvider;

	@Mock
	private ObjectMapper objectMapper;

	@Mock
	private HttpServletResponse response;

	@Mock
	private OAuth2AuthenticationToken authentication;

	@Test
	void onAuthenticationSuccess_정상발급_테스트() throws Exception {
		CustomOAuth2User oAuth2User = new CustomOAuth2User(1L, "user@example.com", "nickname");

		when(authentication.getPrincipal()).thenReturn(oAuth2User);
		when(jwtProvider.createAccessToken(anyLong(), anyString(), anyString()))
			.thenReturn("Bearer accessToken");
		when(jwtProvider.createRefreshToken(anyLong()))
			.thenReturn("Bearer refreshToken");

		doNothing().when(jwtProvider).storeRefreshToken(anyString(), anyString());
		when(objectMapper.writeValueAsString(any())).thenReturn("{\"accessToken\":\"Bearer accessToken\"}");

		MockHttpServletResponse servletResponse = new MockHttpServletResponse();

		successHandler.onAuthenticationSuccess(null, servletResponse, authentication);

		assertEquals("application/json", servletResponse.getContentType());
		assertTrue(servletResponse.getContentAsString().contains("accessToken"));
		assertNotNull(servletResponse.getCookie("refreshToken"));
	}
}
