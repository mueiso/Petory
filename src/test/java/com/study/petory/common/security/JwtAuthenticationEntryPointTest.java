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
import org.springframework.security.core.AuthenticationException;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationEntryPointTest {

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	@Mock
	private AuthenticationException authException;

	@InjectMocks
	private JwtAuthenticationEntryPoint entryPoint;

	@Test
	void commence_인증예외시_401상태코드와_JSON응답을_리턴() throws Exception {

		/* [given]
		 * 테스트 중 응답 내용을 확인하기 위한 StringWriter 와 PrinterWriter 준비
		 */
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);

		// response.getWriter()가 호출되면 내가 만든 printWriter 를 반환하도록 설정
		when(response.getWriter()).thenReturn(printWriter);

		/* [when]
		 * 테스트 대상 메서드 호출 (Authentication 예외 발생 상황 시뮬레이션)
		 */
		entryPoint.commence(request, response, authException);

		/* [then]
		 * 응답 상태 코드가 401 UNAUTHORIZED 로 설정되었는지 확인
		 * 응답 Content-Type 이 JSON 형식인지 확인
		 */
		verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		verify(response).setContentType("application/json;charset=UTF-8");

		// 응답 본문 출력을 버퍼에 반영 (flush 해야 StringWriter 에 데이터가 쓰임)
		printWriter.flush();

		// 버퍼에 출력된 내용을 문자열로 추출
		String result = stringWriter.toString();

		// 출력된 JSON 문자열이 기대한 응답값과 일치하는지 검증
		assertEquals("{\"status\":401,\"message\":\"인증이 필요합니다.\"}", result);
	}
}
