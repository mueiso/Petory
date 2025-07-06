package com.study.petory.domain.user.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import javax.swing.text.html.HTML;

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

		// 응답 상태와 컨텐츠 타입 설정
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json;charset=UTF-8");

		// 헤더로 토큰 전달
		response.setHeader("Authorization", tokens.getAccessToken());
		response.setHeader("X-Refresh-Token", tokens.getRefreshToken());

		// 헤더를 JS 에서 읽을 수 있도록 CORS expose 설정 필요
		response.setHeader("Access-Control-Expose-Headers", "Authorization, X-Refresh-Token");

		// 로그인 성공 후 상대 경로로 리다이렉트
		response.sendRedirect("/");

	}
}
