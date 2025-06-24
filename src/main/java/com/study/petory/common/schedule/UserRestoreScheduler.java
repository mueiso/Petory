package com.study.petory.common.schedule;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.entity.UserStatus;
import com.study.petory.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserRestoreScheduler {

	private final UserRepository userRepository;

	@Scheduled(cron = "0 0 4 * * *", zone = "Asia/Seoul")  // 매일 새벽 4시: 계정 정지 유저 복구 실행
	@Transactional
	// 유저 자동 복구 메서드 (관리자에 의해 정지된 계정 30일 후 자동 복구)
	public void restoreSuspendedUsers() {

		// 30일 전 날짜를 기준으로 복구 시점 설정
		LocalDateTime reactivationTime = LocalDateTime.now().minusDays(30);

		// SUSPENDED 상태이면서 deletedAt 이 30일 이상 된 유저
		List<User> suspendedUsers = userRepository.findByUserStatusAndDeletedAtBefore(
			UserStatus.SUSPENDED,
			reactivationTime);

		for (User user : suspendedUsers) {
			user.restoreEntity();
			user.updateStatus(UserStatus.ACTIVE);

			log.info("[알림] 30일 정지됐던 계정 복구 - userId: {}, email: {}", user.getId(), user.getEmail());
		}
	}

	// TEST 관리자에 의해 정지된 계정 30일 후 자동 복구되는지 바로 확인하기 위한 테스트용 메서드
	@Transactional
	public void testRestoreSuspendedUsers(LocalDateTime simulatedNow) {

		LocalDateTime reactivationTime = simulatedNow.minusDays(30);

		List<User> suspendedUsers = userRepository.findByUserStatusAndDeletedAtBefore(
			UserStatus.SUSPENDED,
			reactivationTime);

		for(User user : suspendedUsers) {
			user.restoreEntity();
			user.updateStatus(UserStatus.ACTIVE);

			log.info("[테스트 알림] 30일 정지됐던 계정 복구 - userId: {}, email: {}", user.getId(), user.getEmail());
		}
	}
}
