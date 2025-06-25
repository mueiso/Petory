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

	// 유저 자동 복구 메서드 (관리자에 의해 정지된 계정 30일 후 자동 복구)
	@Scheduled(cron = "0 0 4 * * *", zone = "Asia/Seoul")  // 매일 새벽 4시: 계정 정지 유저 복구 실행
	public void restoreSuspendedUsers() {

		userSchedulerService.restoreSuspendedUsers();
	}
}
