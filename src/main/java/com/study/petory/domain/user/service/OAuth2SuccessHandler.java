package com.study.petory.domain.user.service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.petory.domain.user.dto.TokenResponseDto;
import com.study.petory.domain.user.entity.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

	private final AuthServiceImpl authServiceImpl;
	private final ObjectMapper objectMapper = new ObjectMapper();  // JSON 변환용

	@Override
	public void onAuthenticationSuccess(
		HttpServletRequest request,
		HttpServletResponse response,
		Authentication authentication) throws IOException, ServletException {

		// OAuth2 로그인 성공 후 DefaultOAuth2User 에서 사용자 속성 추출
		DefaultOAuth2User oauthUser = (DefaultOAuth2User)authentication.getPrincipal();
		Map<String, Object> attributes = oauthUser.getAttributes();

		// 이메일과 닉네임 추출 (구글 기준)
		String email = (String)attributes.get("email");
		String name = (String)attributes.get("name");

		User user = User.builder()
			.email(email)
			.nickname(name)
			.build();

		// 토큰 발급 및 저장 처리
		TokenResponseDto tokens = authServiceImpl.issueToken(user);

		// JSON 응답 설정
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json;charset=UTF-8");

		// accessToken + refreshToken 을 JSON 응답으로 전달
		objectMapper.writeValue(response.getWriter(), tokens);
	}
}
