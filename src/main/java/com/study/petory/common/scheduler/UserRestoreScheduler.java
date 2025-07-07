package com.study.petory.common.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.study.petory.common.service.UserSchedulerService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserRestoreScheduler {

	private final UserSchedulerService userSchedulerService;

	// 유저 자동 복구 스케줄러
	@Scheduled(cron = "0 0 5 * * *", zone = "Asia/Seoul")
	public void restoreSuspendedUsers() {

		userSchedulerService.restoreSuspendedUsers();
	}
}
