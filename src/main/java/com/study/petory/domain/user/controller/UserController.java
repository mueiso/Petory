package com.study.petory.domain.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.study.petory.common.exception.enums.SuccessCode;
import com.study.petory.common.response.CommonResponse;
import com.study.petory.common.security.CustomPrincipal;
import com.study.petory.common.security.JwtProvider;
import com.study.petory.domain.user.dto.UpdateUserRequestDto;
import com.study.petory.domain.user.dto.UserProfileResponseDto;
import com.study.petory.domain.user.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;
	private final JwtProvider jwtProvider;

	/**
	 * 현재 로그인된 사용자 정보를 조회합니다.
	 * @param currentUser 현재 SecurityContext 에 저장된 사용자 정보 (ID, 이메일, 닉네임 등)
	 * @return 성공 시 사용자 프로필 정보와 함께 응답 반환
	 */
	@GetMapping("/me")
	public ResponseEntity<CommonResponse<UserProfileResponseDto>> getMyInfo(
		@AuthenticationPrincipal CustomPrincipal currentUser
	) {
		// currentUser.getId(), currentUser.getEmail(), currentUser.getNickname() 사용
		var profile = userService.getMyProfile(currentUser.getEmail());
		return CommonResponse.of(SuccessCode.FOUND, profile);
	}

	/**
	 * 닉네임 등 사용자 정보 수정
	 * @param request
	 * @param updateDto
	 * @return 수정 성공 메시지만 반환
	 */
	@PatchMapping("/update")
	public ResponseEntity<CommonResponse<Object>> updateUser(
		HttpServletRequest request,
		@Validated @RequestBody UpdateUserRequestDto updateDto) {

		String accessToken = jwtProvider.resolveToken(request);
		String email = jwtProvider.getEmailFromToken(accessToken);

		userService.updateProfile(email, updateDto);

		return CommonResponse.of(SuccessCode.UPDATED);
	}

	/**
	 * 회원 탈퇴
	 * @param request
	 * @return 탈퇴 성공 메시지만 반환
	 */
	@DeleteMapping("/delete")
	public ResponseEntity<CommonResponse<Object>> deleteUser(HttpServletRequest request) {

		String accessToken = jwtProvider.resolveToken(request);
		String email = jwtProvider.getEmailFromToken(accessToken);

		userService.deleteAccount(email);

		return CommonResponse.of(SuccessCode.USER_DELETED);
	}
}
