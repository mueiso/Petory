package com.study.petory.common.security;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.PrintWriter;
import java.io.StringWriter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

@ExtendWith(MockitoExtension.class)
class CustomAccessDeniedHandlerTest {

	@Mock
	private HttpServletRequest request; // 가짜 요청

	@Mock
	private HttpServletResponse response; // 가짜 응답

	@Mock
	private AccessDeniedException accessDeniedException; // 가짜 예외

	@InjectMocks
	private CustomAccessDeniedHandler accessDeniedHandler; // 테스트 대상 클래스

	@Test
	void handle_인가실패시_403상태코드와_JSON응답을_리턴() throws Exception {

		// given
		StringWriter stringWriter = new StringWriter(); // 응답 본문을 메모리에 쓰기 위한 객체
		PrintWriter printWriter = new PrintWriter(stringWriter);
		when(response.getWriter()).thenReturn(printWriter); // response.getWriter() 모킹

		// when
		accessDeniedHandler.handle(request, response, accessDeniedException); // 테스트 대상 메서드 실행

		// then
		verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN); // 403 응답 설정 검증
		verify(response).setContentType("application/json;charset=UTF-8"); // Content-Type 설정 검증

		printWriter.flush(); // 출력 버퍼를 stringWriter로 밀어넣음
		String result = stringWriter.toString(); // 응답 본문 가져오기

		// 응답 본문 검증
		assertEquals("{\"status\":403,\"message\":\"권한이 필요합니다.\"}", result);
	}
}
