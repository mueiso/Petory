package com.study.petory.common.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.study.petory.common.service.UserSchedulerService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserRestoreScheduler {

	private final UserSchedulerService userSchedulerService;

	// 유저 자동 복구 스케줄러 (userStatus = SUSPENDED, deletedAt = 현 시각 기준 30일 된 유저)
	@Scheduled(cron = "0 0 5 * * *", zone = "Asia/Seoul")  // 매일 새벽 5시: 계정 정지 유저 복구 실행
	public void restoreSuspendedUsers() {

		userSchedulerService.restoreSuspendedUsers();
	}
}
