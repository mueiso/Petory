package com.study.petory.domain.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.study.petory.common.exception.enums.SuccessCode;
import com.study.petory.common.response.CommonResponse;
import com.study.petory.common.security.CustomPrincipal;
import com.study.petory.domain.user.dto.TokenResponseDto;
import com.study.petory.domain.user.dto.UpdateUserRequestDto;
import com.study.petory.domain.user.dto.UserProfileResponseDto;
import com.study.petory.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	/**
	 * [테스트 전용 - 로그인]
	 * userId를 기준으로 로그인
	 * 비활성화된 유저는 로그인 불가 예외 발생
	 *
	 * @param userId 로그인 시도할 해당 유저 ID
	 * @return accessToken, refreshToken
	 */
	@PostMapping("/test-login")
	public ResponseEntity<CommonResponse<TokenResponseDto>> testLogin(
		@RequestParam Long userId) {

		TokenResponseDto tokens = userService.testLogin(userId);

		return CommonResponse.of(SuccessCode.USER_LOGIN, tokens);
	}

	/**
	 * [유저 프로필 조회]
	 * 현재 로그인된 사용자의 프로필 정보를 조회합니다.
	 *
	 * @param currentUser 현재 SecurityContext 에 저장된 사용자 정보
	 * @return 성공 시 사용자 프로필 정보와 함께 200 OK 응답
	 */
	@GetMapping("/me")
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	public ResponseEntity<CommonResponse<UserProfileResponseDto>> getMyInfo(
		@AuthenticationPrincipal CustomPrincipal currentUser) {

		// currentUser.getId(), currentUser.getEmail(), currentUser.getNickname() 사용 가능
		UserProfileResponseDto profile = userService.getMyProfile(currentUser.getEmail());

		return CommonResponse.of(SuccessCode.FOUND, profile);
	}

	/**
	 * [유저 프로필 수정]
	 * 현재 로그인된 사용자의 프로필을 업데이트합니다.
	 *
	 * @param currentUser 현재 SecurityContext 에 저장된 사용자 정보
	 * @param updateDto   변경할 사용자 정보 DTO
	 * @return 성공 시 200 OK 응답
	 */
	@PutMapping("/update")
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	public ResponseEntity<CommonResponse<Object>> updateUser(
		@AuthenticationPrincipal CustomPrincipal currentUser,
		@Validated @RequestBody UpdateUserRequestDto updateDto) {

		userService.updateProfile(currentUser.getEmail(), updateDto);

		return CommonResponse.of(SuccessCode.UPDATED);
	}

	/**
	 * [계정 삭제]
	 * 현재 로그인된 사용자의 계정을 삭제합니다.
	 *
	 * @param currentUser 현재 SecurityContext 에 저장된 사용자 정보
	 * @return 성공 시 200 OK 응답
	 */
	@DeleteMapping("/delete")
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	public ResponseEntity<CommonResponse<Object>> deleteUser(
		@AuthenticationPrincipal CustomPrincipal currentUser) {

		userService.deleteAccount(currentUser.getEmail());

		return CommonResponse.of(SuccessCode.USER_DELETED);
	}
}
