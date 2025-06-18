package com.study.petory.domain.user.controller;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.study.petory.common.exception.enums.SuccessCode;
import com.study.petory.common.response.CommonResponse;
import com.study.petory.common.service.EmailService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestMailController {

	private final EmailService emailService;

	@PostMapping("/send-email")
	public ResponseEntity<CommonResponse<Object>> sendTestMail(
		@RequestParam String email,
		@RequestParam String name) {

		emailService.sendDeletionWarning(email, name,
			LocalDateTime.now().minusDays(85));  // 계정이 85일 전에 비활성화 되었다고 가정 → 5일 후에 계정 삭제될거라는 메일 발송
		return CommonResponse.of(SuccessCode.EMAIL_SENT);
	}



}
