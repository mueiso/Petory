package com.study.petory.common.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.study.petory.common.service.UserSchedulerService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserDeletionScheduler {

	private final UserSchedulerService userSchedulerService;

	// 유저 삭제 안내 이메일 자동 발송 스케줄러
	@Scheduled(cron = "0 0 3 * * *", zone = "Asia/Seoul")
	public void sendDeletionWarningEmails() {

		userSchedulerService.sendDeletionWarningEmails();
	}

	// 유저 자동 hardDelete 스케줄러
	@Scheduled(cron = "0 0 4 * * *", zone = "Asia/Seoul")
	public void hardDeleteExpiredUsers() {

		userSchedulerService.hardDeleteExpiredUsers();
	}
}
