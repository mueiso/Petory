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

import com.study.petory.domain.user.dto.TokenResponseDto;
import com.study.petory.domain.user.entity.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

	private final AuthService authService;

	@Override
	public void onAuthenticationSuccess(
		HttpServletRequest request,
		HttpServletResponse response,
		Authentication authentication) throws IOException, ServletException {

		// OAuth2 로그인 성공 후 DefaultOAuth2User 에서 사용자 속성 추출
		DefaultOAuth2User oauthUser = (DefaultOAuth2User) authentication.getPrincipal();
		Map<String, Object> attributes = oauthUser.getAttributes();

		// 이메일과 닉네임 추출 (구글 기준)
		String email = (String) attributes.get("email");
		String nickname = (String) attributes.get("name");

		User user = User.builder()
			.email(email)
			.nickname(nickname)
			.build();

		// 토큰 발급 및 저장 처리
		TokenResponseDto tokens = authService.issueToken(user);

		// URL 인코딩 적용
		String encodedRefreshToken = URLEncoder.encode(tokens.getRefreshToken(), StandardCharsets.UTF_8);
		// Refresh Token 은 보안상 쿠키로 저장 (HttpOnly, Secure, Path=/, Max-Age=7일)
		Cookie refreshTokenCookie = new Cookie("refreshToken", encodedRefreshToken);
		refreshTokenCookie.setHttpOnly(true);
		// TODO - 로컬 개발 중이면 HTTPS 가 아니라서 쿠키가 아예 전송되지 않을 수 있어서 false → 배포 전에 true 로 변경
		refreshTokenCookie.setSecure(true);  // HTTPS 환경에서만 전송 (운영 배포 시 필수)
		refreshTokenCookie.setPath("/");
		refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60);  // 7일 (초 단위)

		response.addCookie(refreshTokenCookie);

		/* TODO - 배포된 프론트엔트 주소로 변경 필요 (예: "https://www.petory.com/oauth/success")
		 * UriComponentsBuilder 사용
		 * accessToken 만 URL 파라미터로 전달
		 * 클라이언트 리다이렉트 (프론트에서 토큰 받을 수 있도록 쿼리 파라미터 전달)
		 */
		String targetUrl = UriComponentsBuilder
			.fromUriString("http://localhost:8080/login-success.html")
			.queryParam("accessToken", tokens.getAccessToken())
			.build()
			.toUriString();

		response.sendRedirect(targetUrl);

	}
}
