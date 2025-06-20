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
public class UserAutoDeactivationScheduler {

	private final UserRepository userRepository;

	@Scheduled(cron = "0 0 1 * * ?", zone = "Asia/Seoul")  // 매일 새벽 1시: 미접속 유저의 계정 휴면처리 실행
	@Transactional
	public void deactivateInactiveUsers() {

		// 90일전 날짜를 기준으로 비활성화 시점 설정 (= 90일간 미접속 시)
		LocalDateTime inactivationTime = LocalDateTime.now().minusDays(90);

		// ACTIVE 상태이면서 90일간 updatedAt 의 변화가 없는 유저
		List<User> deactivationCandidates = userRepository.findByUserStatusAndUpdatedAtBefore(
			UserStatus.ACTIVE,
			inactivationTime);

		for (User user : deactivationCandidates) {
			user.deactivateEntity();
			user.updateStatus(UserStatus.DEACTIVATED);

			log.info("[알림] 90일 미접속 유저 휴면처리 - userId: {}, email: {}", user.getId(), user.getEmail());
		}
	}

	// TEST 90알간 미접속 시 자동 휴면 계정으로 전환되는지 바로 확인하기 위한 테스트용 메서드
	@Transactional
	public void testDeactivateInactiveUsers(LocalDateTime simulatedNow) {

		LocalDateTime inactivationTime = simulatedNow.minusDays(90);

		List<User> deactivationCandidates = userRepository.findByUserStatusAndUpdatedAtBefore(
			UserStatus.ACTIVE,
			inactivationTime);

		for (User user : deactivationCandidates) {
			user.deactivateEntity();
			user.updateStatus(UserStatus.DEACTIVATED);

			log.info("[테스트 알림] 90일 미접속 유저 휴면처리 - userId: {}, email: {}", user.getId(), user.getEmail());
		}
	}
}
