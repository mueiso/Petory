// package com.study.petory.domain.user.controller;
//
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;
//
// import com.study.petory.common.exception.enums.SuccessCode;
// import com.study.petory.common.response.CommonResponse;
// import com.study.petory.domain.user.dto.EmailVerificationConfirmRequestDto;
// import com.study.petory.domain.user.dto.EmailVerificationRequestDto;
// import com.study.petory.domain.user.service.EmailVerificationService;
//
// import jakarta.validation.Valid;
// import lombok.RequiredArgsConstructor;
//
// @RestController
// @RequestMapping("/users")
// @RequiredArgsConstructor
// public class EmailVerificationController {
//
// 	private final EmailVerificationService emailVerificationService;
//
// 	/**
// 	 * [이메일 인증 코드 요청]
// 	 * 클라이언트가 이메일을 보내면 인증 코드를 발송합니다.
// 	 *
// 	 * @param request 사용자의 이메일 정보를 담고 있는 DTO (예: {"email": "user@example.com"})
// 	 * @return 이메일 전송 성공 메시지
// 	 */
// 	@PostMapping("/verify-email")
// 	public ResponseEntity<CommonResponse<Object>> sendVerificationCode(
// 		@RequestBody @Valid EmailVerificationRequestDto request) {
//
// 		emailVerificationService.sendVerificationCode(request.getEmail());
//
// 		return CommonResponse.of(SuccessCode.EMAIL_SENT);
// 	}
//
// 	/**
// 	 * [인증 코드 검증]
// 	 * 사용자가 이메일과 인증 코드를 함께 보내면 Redis 에 저장된 값과 비교해 검증합니다.
// 	 *
// 	 * @param request 사용자의 이메일과 인증 코드 정보를 담은 DTO (예: {"email": "user@example.com", "code": "123456"})
// 	 * @return 인증 성공 메시지
// 	 */
// 	@PostMapping("/verify-email/code")
// 	public ResponseEntity<CommonResponse<Object>> verifyCode(
// 		@RequestBody @Valid EmailVerificationConfirmRequestDto request) {
//
// 		emailVerificationService.verifyCode(request.getEmail(), request.getCode());
//
// 		return CommonResponse.of(SuccessCode.VERIFIED);
// 	}
// }
