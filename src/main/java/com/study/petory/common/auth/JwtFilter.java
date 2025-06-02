package com.study.petory.common.auth;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
// OncePerRequestFilter 를 상속받아 HTTP 요청당 한 번만 실행
public class JwtFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;

	// TODO - URL 추가 필요
	// WHITELIST
	private static final List<String> WHITELIST = List.of(
		"/auth/login",
		"/users/signup",
		"/auth/refresh"
	);

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		// HttpServletRequest 에서 요청된 URI 경로를 문자열로 추출
		String url = request.getRequestURI();

		// 요청된 URL 이 인증 우회 대상인지 판단 후 JWT 인증 필터 건너뛰기/ 후 다음 필터로 넘김
		if(WHITELIST.contains(url)) {
			filterChain.doFilter(request, response);
			return;
		}

		// 클라이언트가 HTTP 요청에 Authorization 헤더 포함시켰는지 확인
		String bearerJwt = request.getHeader("Authorization");

		if(bearerJwt == null) {
			writeErrorResponse(response, 401, "Authorization 헤더가 존재하지 않습니다.");
			return;
		}

	}

	// JWT 없거나 잘못된 경우 사용할 공통 메서드
	private void writeErrorResponse(HttpServletResponse response, HttpStatus status, String message) throws IOException {

		response.setStatus(status.value());
		response.setContentType("application/json;charset=UTF-8");
		response.getWriter().write(String.format("{\"status\":%d,\"message\":\"%s\"}", status.value(), message));
	}
}
