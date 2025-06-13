package com.study.petory.domain.user.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.study.petory.common.exception.enums.SuccessCode;
import com.study.petory.common.response.CommonResponse;
import com.study.petory.common.security.CustomPrincipal;
import com.study.petory.domain.user.dto.TokenResponseDto;
import com.study.petory.domain.user.entity.Role;
import com.study.petory.domain.user.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	/**
	 * [토큰 재발급]
	 * AccessToken 이 만료된 경우에만 RefreshToken 을 기반으로 새로운 AccessToken + RefreshToken 을 재발급
	 * AccessToken 은 "Authorization" 헤더로 전달
	 * RefreshToken 은 "Authorization-Refresh" 헤더로 전달
	 *
	 * @param accessToken 클라이언트가 보유한 기존 AccessToken (만료 상태)
	 * @param refreshToken 클라이언트가 보유한 RefreshToken (Redis 에 저장된 것과 비교됨)
	 * @return 새로 발급된 AccessToken + RefreshToken 을 포함한 응답
	 */
	@PostMapping("/reissue")
	public ResponseEntity<CommonResponse<TokenResponseDto>> reissue(
		@RequestHeader("Authorization") String accessToken,
		@RequestHeader("Authorization-Refresh") String refreshToken
	) {

		TokenResponseDto tokenResponseDto = authService.reissue(accessToken, refreshToken);
		return CommonResponse.of(SuccessCode.TOKEN_REISSUE, tokenResponseDto);
	}

	/**
	 * [로그아웃 처리]
	 * AccessToken 을 블랙리스트에 등록하고,
	 * Redis 에 저장된 RefreshToken 삭제
	 *
	 * @param bearerToken : "Bearer {accessToken}" 형식의 헤더
	 * @return 로그아웃 성공 메시지
	 */
	@DeleteMapping("/logout")
	public ResponseEntity<CommonResponse<Object>> logout(@RequestHeader("Authorization") String bearerToken) {

		authService.logout(bearerToken);
		return CommonResponse.of(SuccessCode.USER_LOGOUT);
	}

	/**
	 * [관리자 전용 - 권한 추가]
	 * ADMIN 권한이 있는 사용자만 다른 사용자에게 권한을 부여할 수 있음
	 *
	 * @param targetUserId 권한을 부여할 사용자 ID
	 * @param role 부여할 권한
	 * @param currentUser 현재 로그인한 관리자 (자동 주입)
	 * @return 부여 이후 해당 사용자의 전체 권한 목록
	 */
	@PostMapping("/role")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<CommonResponse<Object>> addUserRole(
		@RequestParam("userId") Long targetUserId,
		@RequestParam("role") Role role,
		@AuthenticationPrincipal CustomPrincipal currentUser
	) {

		List<Role> updatedRoles = authService.addRoleToUser(targetUserId, role);
		return CommonResponse.of(SuccessCode.UPDATED, updatedRoles);
	}

	/**
	 * [관리자 전용 - 권한 제거]
	 * ADMIN 권한이 있는 사용자만 다른 사용자의 권한을 제거할 수 있음
	 *
	 * @param targetUserId 권한 제거당할 사용자 ID
	 * @param role 제거할 권한
	 * @param currentUser 현재 로그인한 관리자
	 * @return 제거 이후 해당 사용자의 전체 권한 목록
	 */
	@DeleteMapping("/role/remove")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<CommonResponse<Object>> removeUserRole(
		@RequestParam("userId") Long targetUserId,
		@RequestParam("role") Role role,
		@AuthenticationPrincipal CustomPrincipal currentUser
	) {

		List<Role> updatedRoles = authService.removeRoleFromUser(targetUserId, role);
		return CommonResponse.of(SuccessCode.DELETED, updatedRoles);
	}
}
