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
import com.study.petory.common.schedule.UserRestoreScheduler;
import com.study.petory.common.service.EmailService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestSchedulerController {

	private final EmailService emailService;
	private final UserDeletionScheduler userDeletionScheduler;
	private final UserRestoreScheduler userRestoreScheduler;

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
	 * 스테줄러에 맞춰 메일 자동 발송되는지 바로 확인하기 위한 테스트용 API
	 *
	 * @param date 이메일 발송 예정 날짜
	 * @return 이메일 발송 성공 메시지
	 */
	@PostMapping("/send-deletion-warning")
	public ResponseEntity<CommonResponse<Object>> testAutoDeletionWarning(
		@RequestParam String date) {

		// 현재 날짜를 임의로 설정해서 테스트 (예: 이메일 발송 예정 날짜인 "2025-06-18T00:00")
		LocalDateTime simulatedNow = LocalDateTime.parse(date);

		userDeletionScheduler.testSendDeletionWarningEmails(simulatedNow);

		return CommonResponse.of(SuccessCode.EMAIL_SENT);
	}

	/**
	 * [TEST]
	 * 스케줄러에 맞춰 soft delete 된 지 90일 초과된 유저 자동 hard delete 되는지 확인할 수 있는 테스트용 API
	 * DELETE 매핑이 아닌 POST 매핑인 이유: 직접 삭제하는 행위보다, 자동 삭제하는 로직을 수동으로 실행하는 트리거성 API 이기 때문
	 *
	 * @param date 유저 영구 삭제 예정 날짜
	 * @return 삭제 성공 메시지
	 */
	@PostMapping("/delete-expired-users")
	public ResponseEntity<CommonResponse<Object>> testAutoHardDelete(
		@RequestParam String date) {

		// 현재 날짜를 임의로 설정해서 테스트 (예: Hard delete 예정 날짜인 "2025-06-18T00:00")
		LocalDateTime simulatedNow = LocalDateTime.parse(date);

		userDeletionScheduler.testHardDeleteExpiredUsers(simulatedNow);

		return CommonResponse.of(SuccessCode.DELETED);
	}

	/**
	 * [TEST]
	 * 스케줄러에 맞춰 계정이 정지된 지 30일 결과한 유저 자동으로 계정 복구 되는지 확인할 수 있는 테스트용 API
	 *
	 * @param date 계정 복구 예정 날짜
	 * @return 복구 성공 메시지
	 */
	@PostMapping("/restore-suspended-users")
	public ResponseEntity<CommonResponse<Object>> testAutoRestore(
		@RequestParam String date) {

		// 현재 날짜를 임의로 설정해서 테스트 (예: 복구 예정 날짜인 "2025-06-18T00:00")
		LocalDateTime simulatedNow = LocalDateTime.parse(date);

		userRestoreScheduler.testRestoreSuspendedUsers(simulatedNow);

		return CommonResponse.of(SuccessCode.RESTORED);
	}
}
