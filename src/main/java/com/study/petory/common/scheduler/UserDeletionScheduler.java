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

	// 유저 삭제 안내 이메일 자동 발송 스케줄러 메서드 (userStatus = DEACTIVATED, deletedAt = 현 시각 기준 85~89일 지난 유저)
	@Scheduled(cron = "0 0 2 * * *", zone = "Asia/Seoul")  // 매일 새벽 2시: 삭제 예정 알림 (한국 시간대 기준)
	public void sendDeletionWarningEmails() {

		userSchedulerService.sendDeletionWarningEmails();
		// log.info("[알림] 삭제 예정 유저에게 이메일 전송 - email: {}", email);
	}

	// 유저 자동 hardDelete 스케줄러 (userStatus = DEACTIVATE or DELETED, deletedAt = 현 시각 기준 90일 이상 지난 유저)
	@Scheduled(cron = "0 0 3 * * *", zone = "Asia/Seoul")  // 매일 새벽 3시: hard delete 실행 (한국 시간대 기준)
	public void hardDeleteExpiredUsers() {

		userSchedulerService.hardDeleteExpiredUsers();
		// log.info("[알림] 휴면 계정 90일 초과된 유저 삭제 - userId: {}, email: {}", user.getId(), user.getEmail());
	}
}
