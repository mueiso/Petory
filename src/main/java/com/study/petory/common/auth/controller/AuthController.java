package com.study.petory.common.auth.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nimbusds.oauth2.sdk.TokenResponse;
import com.study.petory.common.auth.dto.OAuth2LoginRequestDto;
import com.study.petory.common.auth.service.AuthService;
import com.study.petory.common.response.CommonResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	// OAuth2 로그인 후 토큰 발급
	@PostMapping("/login/oauth2")
	public CommonResponse<TokenResponse> oauth2Login(@RequestBody OAuth2LoginRequestDto loginRequestDto) {
		TokenResponse tokenResponse = authService.issueToken(loginRequestDto.toUser());
		return CommonResponse.of(SuccessCode.SUCCESS_USER_LOGIN, tokenResponse);
	}

	// 로그아웃 처리
	@DeleteMapping("/logout")
	public CommonResponse<Object> logout(@RequestHeader("Authorization") String bearerToken,
		@RequestParam String email) {
		authService.logout(bearerToken, email);
		return CommonResponse.of(SuccessCode.SUCCESS_USER_LOGOUT);
	}

	// Refresh Token 으로 Access Token 재발급
	@PostMapping("/reissue")
	public CommonResponse<TokenResponse> reissue(@RequestBody TokenReissueRequest request) {
		TokenResponse tokenResponse = authService.reissue(request.getEmail(), request.getRefreshToken());
		return CommonResponse.of(SuccessCode.SUCCESS_TOKEN_REISSUE, tokenResponse);
	}
}

