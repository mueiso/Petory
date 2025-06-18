package com.study.petory.domain.user.controller;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.study.petory.common.exception.enums.SuccessCode;
import com.study.petory.common.response.CommonResponse;
import com.study.petory.common.schedule.UserDeletionScheduler;
import com.study.petory.common.service.EmailService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestMailController {

	private final EmailService emailService;
	private final UserDeletionScheduler userDeletionScheduler;

	/**
	 * [TEST]
	 * 메일 발송 성공적으로 되는지 테스트용 API
	 *
	 * @param email 메일 받을 이메일 주소 (임의로 설정)
	 * @param name 메일 받는 사람 이름 (임의로 설정)
	 * @return 이메일 발송 성공 메시지
	 */
	@PostMapping("/send-email")
	public ResponseEntity<CommonResponse<Object>> testSendMail(
		@RequestParam String email,
		@RequestParam String name) {

		// 계정이 85일 전에 비활성화 되었다고 가정 → 5일 후에 계정 삭제될거라는 메일 발송
		emailService.sendDeletionWarning(email, name, LocalDateTime.now().minusDays(85));

		return CommonResponse.of(SuccessCode.EMAIL_SENT);
	}

	/**
	 * [TEST]
	 * 스테줄러에 맞춰 메일 자동 발송 되는지 바로 확인하기 위한 테스트용 API
	 *
	 * @param date 이메일 발송 예정 날짜
	 * @return 이메일 발송 성공 메시지
	 */
	@PostMapping("/send-deletion-warning")
	public ResponseEntity<CommonResponse<Object>> testAutoDeletionWarning(
		@RequestParam String date) {

		// 현재 날짜를 임의로 조작해서 테스트 (예: 이메일 발송 예정 날짜인 "2025-06-18T00:00")
		LocalDateTime simulatedNow = LocalDateTime.parse(date);

		userDeletionScheduler.testSendDeletionWarningEmails(simulatedNow);

		return CommonResponse.of(SuccessCode.EMAIL_SENT);
	}
}
