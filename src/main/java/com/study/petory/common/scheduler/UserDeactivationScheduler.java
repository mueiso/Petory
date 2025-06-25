package com.study.petory.common.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.study.petory.common.service.UserSchedulerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserDeactivationScheduler {

	private final UserSchedulerService userSchedulerService;

	// @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")  // 매일 자정
	// @Transactional
	// public void sendDeactivationWarningEmails() {
	//
	// 	LocalDateTime now =
	// }

	// 유저 자동 휴면 전환 스케줄러 (userStatus = ACTIVE, updatedAt = 현 시각 기준 90일 이상 지난 유저)
	@Scheduled(cron = "0 0 1 * * *", zone = "Asia/Seoul")  // 매일 새벽 1시: 미접속 유저의 계정 휴면처리 실행
	@Transactional
	public void deactivateInactiveUsers() {

		userSchedulerService.deactivateInactiveUsers();
		// log.info("[알림] 90일 미접속 유저 휴면처리 - userId: {}, email: {}", user.getId(), user.getEmail());
	}
}
