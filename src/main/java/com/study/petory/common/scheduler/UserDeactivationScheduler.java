package com.study.petory.common.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.study.petory.common.service.UserSchedulerService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserDeactivationScheduler {

	private final UserSchedulerService userSchedulerService;

	// 유저 자동 휴면 전환 안내 이메일 발송 스케줄러
	@Scheduled(cron = "0 0 1 * * *", zone = "Asia/Seoul")
	public void sendDeactivationWarningEmails() {

		userSchedulerService.sendDeactivationWarningEmails();
	}

	// 유저 자동 휴면 전환 스케줄러
	@Scheduled(cron = "0 0 2 * * *", zone = "Asia/Seoul")
	public void deactivateInactiveUsers() {

		userSchedulerService.deactivateInactiveUsers();
	}
}
