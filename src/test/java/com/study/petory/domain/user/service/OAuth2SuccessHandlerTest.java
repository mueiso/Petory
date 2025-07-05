package com.study.petory.domain.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.petory.domain.user.dto.TokenResponseDto;
import com.study.petory.domain.user.entity.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
class OAuth2SuccessHandlerTest {

	@Mock
	private AuthServiceImpl authServiceImpl;

	@InjectMocks
	private OAuth2SuccessHandler oAuth2SuccessHandler;

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	@Test
	void onAuthenticationSuccess_소셜로그인_성공시_토큰_JSON_응답처리() throws Exception {

		/* [given]
		 *
		 * OAuth2 인증 사용자 속성 미리 구성 (이메일, 닉네임)
		 */
		Map<String, Object> attributes = new HashMap<>();
		attributes.put("email", "user@example.com");
		attributes.put("name", "nickname");

		/*
		 * OAuth2User 객체 생성
		 * 권한은 속성 기반으로 생성
		 * 사용자명으로 email 을 key 로 사용
		 */
		OAuth2User oauth2User = new DefaultOAuth2User(
			java.util.List.of(new OAuth2UserAuthority(attributes)),
			attributes,
			"email"
		);

		// Spring Security 의 인증 객체 생성 (OAuth2User 를 principal 로 사용)
		UsernamePasswordAuthenticationToken authentication =
			new UsernamePasswordAuthenticationToken(oauth2User, null, oauth2User.getAuthorities());

		// 테스트용으로 AccessToken, RefreshToken 이 포함된 응답 DTO 구성
		TokenResponseDto tokens = new TokenResponseDto("accessTokenValue", "refreshTokenValue");

		// AuthServiceImpl 의 issueToken 메서드 호출되면 위의 토큰 응답을 리턴하도록 설정 (Mocking)
		when(authServiceImpl.issueToken(any(User.class))).thenReturn(tokens);

		/*
		 * 실제 응답 내용을 테스트하기 위해 StringWriter 와 PrintWriter 를 사용 (response.getWriter() 대체용)
		 * StringWriter: 문자열을 메모리에 저장해 메모리에 출력하기 위한 클래스 (예: .toString())
		 * PrintWriter: 문자열을 콘솔 등에 출력하기 위한 클래스 (예: println())
		 */
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);

		// response.getWriter()가 호출되면 내가 만든 printWriter 를 반환하도록 설정
		when(response.getWriter()).thenReturn(printWriter);

		/* [when]
		 * 실제 테스트 대상인 OAuth2SuccessHandler 의 onAuthenticationSuccess 메서드 실행
		 */
		oAuth2SuccessHandler.onAuthenticationSuccess(request, response, authentication);

		/* [then]
		 * issueToken 메서드가 1회 호출되었는지 검증 (사용자 정보로 토큰 발급 수행)
		 * 응답 상태가 200 OK 로 설정되었는지 확인
		 * Content-Type 이 JSON 이고 UTF-8 인코딩인지 확인
		 */
		verify(authServiceImpl).issueToken(any(User.class));
		verify(response).setStatus(HttpServletResponse.SC_OK);
		verify(response).setContentType("application/json;charset=UTF-8");

		// printWriter 에 실제 응답 데이터 쓰기 위해 flush 수행
		printWriter.flush();

		/*
		 * 응답 결과로 출력된 문자열(JSON)을 읽어오고
		 * JSON 문자열을 다시 TokenResponseDto 객체로 역직렬화 (검증을 위해)
		 */
		String responseJson = stringWriter.toString();
		ObjectMapper mapper = new ObjectMapper();
		TokenResponseDto result = mapper.readValue(responseJson, TokenResponseDto.class);

		/*
		 * AccessToken 값이 예상한 값과 일치하는지 검증
		 * RefreshToken 값이 예상한 값과 일치하는지 검증
		 */
		assertEquals("accessTokenValue", result.getAccessToken());
		assertEquals("refreshTokenValue", result.getRefreshToken());
	}
}
