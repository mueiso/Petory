package com.study.petory.common.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.study.petory.common.service.UserSchedulerService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserDeactivationScheduler {

	private final UserSchedulerService userSchedulerService;

	// 유저 자동 휴면 전환 안내 이메일 발송 스케줄러 (userStatus = ACTIVE, updatedAt = 현 시각 기준 85일 지난 유저)
	@Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")  // 매일 자정: 휴면 예정 알림 (한국 시간대 기준)
	public void sendDeactivationWarningEmails() {

		userSchedulerService.sendDeactivationWarningEmails();
	}

	// 유저 자동 휴면 전환 스케줄러 (userStatus = ACTIVE, updatedAt = 현 시각 기준 90일 이상 지난 유저)
	@Scheduled(cron = "0 0 1 * * *", zone = "Asia/Seoul")  // 매일 새벽 1시: 미접속 유저의 계정 휴면처리 실행
	public void deactivateInactiveUsers() {

		userSchedulerService.deactivateInactiveUsers();
	}
}
