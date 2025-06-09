package com.study.petory.domain.user.service;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
/*
 * AuthenticationFailureHandler: Spring Security 에서 인증 실패 시 실행되는 콜백 핸들러
 * OAuth2 로그인 실패 시 호출되는 핸들러
 */
public class OAuth2FailureHandler implements AuthenticationFailureHandler {

	// 인증 실패 시 실행되는 콜백 메서드
	@Override
	public void onAuthenticationFailure(
		HttpServletRequest request,
		HttpServletResponse response,
		AuthenticationException exception)
		throws IOException, ServletException {

		// 인증 실패 로그를 경고 레벨로 기록
		log.warn("OAuth2 로그인 실패: {}", exception.getMessage());

		// HTTP 상태 코드를 401(Unauthorized)로 설정 (인증 실패를 의미)
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

		// 응답 데이터 형식을 JSON 으로 지정하고 문자 인코딩을 UTF-8로 설정
		response.setContentType("application/json;charset=UTF-8");

		// 응답 본문에 실패 메시지를 JSON 형태로 작성하여 클라이언트에 전달
		response.getWriter().write("{\"status\":401,\"message\":\"소셜 로그인에 실패했습니다. 다시 시도해주세요.\"}");
	}
}
