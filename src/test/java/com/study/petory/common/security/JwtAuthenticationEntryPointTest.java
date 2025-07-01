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

		// given
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		when(response.getWriter()).thenReturn(printWriter);

		// when
		entryPoint.commence(request, response, authException);

		// then
		verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
		verify(response).setContentType("application/json;charset=UTF-8");

		printWriter.flush(); // flush 해야 StringWriter에 데이터가 씀

		String result = stringWriter.toString();
		assertEquals("{\"status\":401,\"message\":\"인증이 필요합니다.\"}", result);
	}
}
