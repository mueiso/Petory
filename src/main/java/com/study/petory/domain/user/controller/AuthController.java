package com.study.petory.domain.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.study.petory.common.exception.enums.SuccessCode;
import com.study.petory.common.response.CommonResponse;
import com.study.petory.domain.user.dto.TokenResponseDto;
import com.study.petory.domain.user.service.AuthService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	/**
	 * [토큰 재발급]
	 * Authorization 헤더에서 refreshToken 을 추출해 Redis 검증 후
	 * AccessToken 과 새로운 RefreshToken 을 재발급
	 * 새 RefreshToken 은 응답 본문에 포함 (쿠키 사용 안함)
	 *
	 * @param bearerRefreshToken : "Bearer {refreshToken}" 형식의 헤더
	 * @return 새로 발급된 AccessToken + RefreshToken 포함 응답
	 */
	@PostMapping("/reissue")
	public ResponseEntity<CommonResponse<TokenResponseDto>> reissue(
		@RequestHeader("Authorization") String bearerRefreshToken
	) {

		TokenResponseDto tokenResponseDto = authService.reissue(bearerRefreshToken);
		return CommonResponse.of(SuccessCode.TOKEN_REISSUE, tokenResponseDto);
	}

	/**
	 * [로그아웃 처리]
	 * AccessToken 을 블랙리스트에 등록하고,
	 * Redis 에 저장된 RefreshToken 삭제
	 *
	 * @param bearerToken : "Bearer {accessToken}" 형식의 헤더
	 * @param response : (쿠키 방식일 경우 제거용)
	 * @return 로그아웃 성공 메시지
	 */
	@DeleteMapping("/logout")
	public ResponseEntity<CommonResponse<Object>> logout(
		@RequestHeader("Authorization") String bearerToken,
		HttpServletResponse response
	) {

		authService.logout(bearerToken);
		return CommonResponse.of(SuccessCode.USER_LOGOUT);
	}
}
