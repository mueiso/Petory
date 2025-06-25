package com.study.petory.common.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.study.petory.common.service.UserSchedulerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserDeletionScheduler {

	private final UserSchedulerService userSchedulerService;

	// 이메일 자동 발송 스케줄러 메서드 (soft delete 된 지 85일 경과 & 89일 미만 유저에게 메일 발송)
	@Scheduled(cron = "0 0 2 * * *", zone = "Asia/Seoul")  // 매일 새벽 2시: 삭제 예정 알림 (한국 시간대 기준)
	public void sendDeletionWarningEmails() {

		userSchedulerService.sendDeletionWarningEmails();
	}

	// 유저 자동 삭제 메서드 (휴면 계쩡 or 탈퇴 계정이 된 지 90일 초과된 유저 자동 hardDelete)
	@Scheduled(cron = "0 0 3 * * *", zone = "Asia/Seoul")  // 매일 새벽 3시: hard delete 실행 (한국 시간대 기준)
	public void hardDeleteExpiredUsers() {

		userSchedulerService.hardDeleteExpiredUsers();
	}
}
