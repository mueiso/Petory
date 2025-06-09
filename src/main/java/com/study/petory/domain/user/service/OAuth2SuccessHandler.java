package com.study.petory.domain.user.service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.study.petory.common.security.JwtProvider;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.repository.UserRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

	private final JwtProvider jwtProvider;
	private final UserRepository userRepository;

	@Override
	public void onAuthenticationSuccess(
		HttpServletRequest request,
		HttpServletResponse response,
		Authentication authentication) throws IOException, ServletException {

		// OAuth2 로그인 후 DefaultOAuth2User 에서 이메일 추출
		String email = authentication.getName(); // 기본 키 설정이 email

		User user = userRepository.findByEmail(email)
			.orElseThrow(() -> new IllegalArgumentException("로그인된 유저 정보를 찾을 수 없습니다."));

		// JWT 토큰 생성
		String accessToken = jwtProvider.createAccessToken(user.getId(), user.getEmail(), user.getNickname());
		String refreshToken = jwtProvider.createRefreshToken(user.getId());

		// RefreshToken Redis 저장
		jwtProvider.storeRefreshToken(user.getEmail(), refreshToken);

		// Refresh Token 은 보안상 쿠키에 저장 (HttpOnly, Secure, Path=/, Max-Age=7일)
		Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
		refreshTokenCookie.setHttpOnly(true);
		// TODO - 로컬 개발 중이면 HTTPS 가 아니라서 쿠키가 아예 전송되지 않을 수 있어서 false → 배포 전에 true 로 변경
		refreshTokenCookie.setSecure(false);  // HTTPS 환경에서만 전송 (운영 배포 시 필수)
		refreshTokenCookie.setPath("/");
		refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60);  // 7일 (초 단위)

		response.addCookie(refreshTokenCookie);

		// TODO - 배포된 프론트엔트 주소로 변경 필요 (예: "https://www.petory.com/oauth/success")
		/*
		 * accessToken 만 URL 파라미터로 전달
		 * 클라이언트로 리다이렉트 (프론트에서 토큰 받을 수 있도록 쿼리 파라미터 전달)
		 */
		String targetUrl = UriBuilder.of("http://localhost:3000/oauth/success")
			.addParam("accessToken", accessToken)
			.build();

		response.sendRedirect(targetUrl);
	}

	// 간단한 URI 빌더 유틸 (추후에 확장성 고려 시 별도 유틸로 분리 가능)
	private static class UriBuilder {
		private final StringBuilder uri;
		private boolean hasParam = false;

		private UriBuilder(String base) {
			this.uri = new StringBuilder(base);
		}

		public static UriBuilder of(String base) {
			return new UriBuilder(base);
		}

		public UriBuilder addParam(String key, String value) {
			if (!hasParam) {
				uri.append("?");
				hasParam = true;
			} else {
				uri.append("&");
			}
			uri.append(URLEncoder.encode(key, StandardCharsets.UTF_8));
			uri.append("=");
			uri.append(URLEncoder.encode(value, StandardCharsets.UTF_8));
			return this;
		}

		public String build() {
			return uri.toString();
		}
	}
}
