package com.study.petory.domain.user.controller;

import com.study.petory.common.response.CommonResponse;
import com.study.petory.domain.user.dto.UserProfileResponseDto;
import com.study.petory.domain.user.dto.UpdateUserRequestDto;
import com.study.petory.domain.user.service.UserService;
import com.study.petory.common.security.JwtProvider;
import com.study.petory.exception.enums.SuccessCode;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;
	private final JwtProvider jwtProvider;

	/**
	 * 내 정보 조회
	 * @param request
	 * @return 성공 메시지 + 내 정보 (이메일, 닉네임)
	 */
	@GetMapping("/me")
	public ResponseEntity<CommonResponse<UserProfileResponseDto>> getMyInfo(HttpServletRequest request) {

		String accessToken = jwtProvider.resolveToken(request);
		String email = jwtProvider.getEmailFromToken(accessToken);

		UserProfileResponseDto profile = userService.getMyProfile(email);

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
