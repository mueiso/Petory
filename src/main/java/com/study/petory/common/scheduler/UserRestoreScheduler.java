package com.study.petory.common.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.study.petory.common.service.UserSchedulerService;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.entity.UserStatus;
import com.study.petory.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserRestoreScheduler {

	private final UserSchedulerService userSchedulerService;

	// 유저 자동 복구 스케줄러 (userStatus = SUSPENDED, deletedAt = 현 시각 기준 30일 된 유저)
	@Scheduled(cron = "0 0 4 * * *", zone = "Asia/Seoul")  // 매일 새벽 4시: 계정 정지 유저 복구 실행
	public void restoreSuspendedUsers() {

		userSchedulerService.restoreSuspendedUsers();
		// log.info("[알림] 30일 정지됐던 계정 복구 - userId: {}, email: {}", user.getId(), user.getEmail());
	}
}
