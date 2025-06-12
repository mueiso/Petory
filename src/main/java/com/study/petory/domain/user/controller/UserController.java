package com.study.petory.domain.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.study.petory.common.exception.enums.SuccessCode;
import com.study.petory.common.response.CommonResponse;
import com.study.petory.common.security.CustomPrincipal;
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
	 * 현재 로그인된 사용자의 프로필 정보를 조회합니다.
	 *
	 * @param currentUser 현재 SecurityContext 에 저장된 사용자 정보
	 * @return 성공 시 사용자 프로필 정보와 함께 200 OK 응답
	 */
	@GetMapping("/me")
	public ResponseEntity<CommonResponse<UserProfileResponseDto>> getMyInfo(
		@AuthenticationPrincipal CustomPrincipal currentUser) {

		// currentUser.getId(), currentUser.getEmail(), currentUser.getNickname() 사용 가능
		UserProfileResponseDto profile = userService.getMyProfile(currentUser.getEmail());
		return CommonResponse.of(SuccessCode.FOUND, profile);
	}

	/**
	 * 현재 로그인된 사용자의 프로필을 업데이트합니다.
	 *
	 * @param currentUser 현재 SecurityContext 에 저장된 사용자 정보
	 * @param updateDto   변경할 사용자 정보 DTO
	 * @return 성공 시 200 OK 응답
	 */
	@PutMapping("/update")
	public ResponseEntity<CommonResponse<Object>> updateUser(
		@AuthenticationPrincipal CustomPrincipal currentUser,
		@Validated @RequestBody UpdateUserRequestDto updateDto
	) {
		userService.updateProfile(currentUser.getEmail(), updateDto);
		return CommonResponse.of(SuccessCode.UPDATED);
	}

	/**
	 * 현재 로그인된 사용자의 계정을 삭제합니다.
	 *
	 * @param currentUser 현재 SecurityContext 에 저장된 사용자 정보
	 * @return 성공 시 200 OK 응답
	 */
	@DeleteMapping("/delete")
	public ResponseEntity<CommonResponse<Object>> deleteUser(
		@AuthenticationPrincipal CustomPrincipal currentUser
	) {
		userService.deleteAccount(currentUser.getEmail());
		return CommonResponse.of(SuccessCode.USER_DELETED);
	}
}
