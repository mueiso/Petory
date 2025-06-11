package com.study.petory.domain.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.study.petory.common.response.CommonResponse;
import com.study.petory.domain.user.dto.OAuth2LoginRequestDto;
import com.study.petory.domain.user.dto.TokenResponseDto;
import com.study.petory.domain.user.service.AuthService;
import com.study.petory.exception.enums.SuccessCode;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	private static final String REFRESH_TOKEN_NAME = "refreshToken";

	@PostMapping("/login")
	public ResponseEntity<CommonResponse<TokenResponseDto>> login(
		@RequestBody OAuth2LoginRequestDto loginRequestDto,
		HttpServletResponse response) {

		// 로그인 및 JWT 토큰 발급
		TokenResponseDto tokenResponseDto = authService.login(loginRequestDto);

		// refreshToken 을 HttpOnly 쿠키로 설정
		Cookie refreshCookie = new Cookie(REFRESH_TOKEN_NAME, tokenResponseDto.getRefreshToken());
		refreshCookie.setPath("/");
		refreshCookie.setHttpOnly(true);
		refreshCookie.setSecure(true);  // HTTPS 환경에서만 전송
		refreshCookie.setMaxAge(7 * 24 * 60 * 60);  // 7일
		response.addCookie(refreshCookie);

		return CommonResponse.of(SuccessCode.USER_LOGIN, tokenResponseDto);
	}

	/**
	 * 로그아웃 처리
	 * AccessToken 을 블랙리스트에 등록
	 * Redis 에서 RefreshToken 제거
	 * 클라이언트의 RefreshToken 쿠키 제거
	 *
	 * @param bearerToken : "Bearer {token}" 형식의 인증 토큰
	 * @param response : HttpServletResponse 객체 (쿠키 제거 위해 사용)
	 * @return 로그아웃 성공 메시지
	 */
	@DeleteMapping("/logout")
	public ResponseEntity<CommonResponse<Object>> logout(
		@RequestHeader("Authorization") String bearerToken,
		HttpServletResponse response) {

		authService.logout(bearerToken);

		// 클라이언트의 refreshToken 쿠키 제거 (MaxAge = 0)
		Cookie deleteCookie = new Cookie(REFRESH_TOKEN_NAME, null);
		deleteCookie.setPath("/");
		deleteCookie.setMaxAge(0);
		deleteCookie.setHttpOnly(true);
		deleteCookie.setSecure(true);  // HTTPS 환경에서만 전송
		response.addCookie(deleteCookie);

		return CommonResponse.of(SuccessCode.USER_LOGOUT);
	}

	/**
	 * RefreshToken 쿠키 기반으로 AccessToken 재발급
	 * Redis 에 저장된 RefreshToken 과 쿠키 비교
	 * AccessToken + 새 RefreshToken 발급
	 * 새 RefreshToken 을 쿠키로 재설정
	 *
	 * @param request : HttpServletRequest (쿠키 접근)
	 * @param response : HttpServletResponse (새 쿠키 설정)
	 * @return 새로 발급된 AccessToken + RefreshToken 포함 응답
	 */
	@PostMapping("/reissue")
	public ResponseEntity<CommonResponse<TokenResponseDto>> reissue(
		HttpServletRequest request,
		HttpServletResponse response) {

		TokenResponseDto tokenResponseDto = authService.reissue(request);

		// 새 refreshToken 을 HttpOnly 쿠키로 설정
		Cookie refreshCookie = new Cookie(REFRESH_TOKEN_NAME, tokenResponseDto.getRefreshToken());
		refreshCookie.setPath("/");
		refreshCookie.setHttpOnly(true);
		refreshCookie.setSecure(true);  // HTTPS 환경
		refreshCookie.setMaxAge(7 * 24 * 60 * 60);  // 7일
		response.addCookie(refreshCookie);

		return CommonResponse.of(SuccessCode.TOKEN_REISSUE, tokenResponseDto);
	}
}
