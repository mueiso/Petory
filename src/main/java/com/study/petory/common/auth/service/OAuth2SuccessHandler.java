package com.study.petory.common.auth.service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.repository.UserRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

	private final JwtProvider jwtProvider;
	private final UserRepository userRepository;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException, ServletException {

		// OAuth2 로그인 후 DefaultOAuth2User에서 이메일 추출
		String email = authentication.getName(); // 기본 키 설정이 email이었음

		User user = userRepository.findByEmail(email)
			.orElseThrow(() -> new IllegalArgumentException("로그인된 유저 정보를 찾을 수 없습니다."));

		// JWT 토큰 발급
		String accessToken = jwtProvider.createAccessToken(user);
		String refreshToken = jwtProvider.createRefreshToken(user);

		// refreshToken 저장 (예: Redis 또는 DB - JwtProvider 내부 또는 Service에서 처리)
		jwtProvider.storeRefreshToken(user.getEmail(), refreshToken);

		// 클라이언트로 리다이렉트 (프론트에서 토큰 받을 수 있도록 쿼리 파라미터 전달)
		String targetUrl = UriBuilder.of("http://localhost:3000/oauth/success")
			.addParam("accessToken", accessToken)
			.addParam("refreshToken", refreshToken)
			.build();

		response.sendRedirect(targetUrl);
	}

	// 간단한 URI 빌더 유틸 (내부 클래스로 사용하거나 별도 유틸로 분리 가능)
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