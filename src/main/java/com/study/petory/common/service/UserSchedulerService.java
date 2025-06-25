package com.study.petory.common.service;

import java.time.LocalDateTime;
import java.util.List;

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
public class UserSchedulerService {

	private static final int ACCOUNT_DELETION_DELAY_DAYS = 90;  // 삭제까지 유예 기간

	private final EmailService emailService;
	private final UserRepository userRepository;

	@Transactional
	public void sendDeletionWarningEmails() {

		LocalDateTime now = LocalDateTime.now();
		testSendDeletionWarningEmails(now);
	}

	@Transactional
	public void hardDeleteExpiredUsers() {

		LocalDateTime now = LocalDateTime.now();
		testHardDeleteExpiredUsers(now);
	}

	@Transactional
	public void restoreSuspendedUsers() {

		LocalDateTime now = LocalDateTime.now();
		testRestoreSuspendedUsers(now);
	}

	// TEST 스테줄러에 맞춰 이메일 자동 발송 되는지 바로 확인하기 위한 테스트용 메서드
	@Transactional
	public void testSendDeletionWarningEmails(LocalDateTime simulatedNow) {

		/*
		 * 1. 현재 시각 기준으로 계산 시작
		 * 2. 89일 전 시점 계산: soft delete '85일 ~ 89일' 사이의 기간을 만들기 위한 계산 (-90+1로 계산하는 이유)
		 * 3. 85일 전 시점 계산
		 */
		LocalDateTime from = simulatedNow.minusDays(ACCOUNT_DELETION_DELAY_DAYS).plusDays(1);
		LocalDateTime to = simulatedNow.minusDays(85);

		// 위 시간 범위 안에서 soft delete 처리된 유저 목록 조회
		List<User> usersToBeDeleted = userRepository.findByUserStatusAndDeletedAtBetween(UserStatus.DEACTIVATED, from,
			to);

		for (User user : usersToBeDeleted) {
			String email = user.getEmail();
			String name = user.getUserPrivateInfo().getName();

			// 이메일 발송 + 삭제 예정일 함께 전달
			emailService.sendDeletionWarning(email, name, user.getDeletedAt());
			log.info("[테스트 알림] 삭제 예정 유저에게 이메일 전송 - email: {}", email);
		}
	}

	// TEST 스케줄러에 맞춰 휴면 계쩡 or 탈퇴 계정이 된 지 90일 초과된 유저 자동 hardDelete 되는지 바로 확인하기 위한 테스트용 메서드
	@Transactional
	public void testHardDeleteExpiredUsers(LocalDateTime simulatedNow) {

		// 90일 전 날짜를 기준으로 삭제 시점 설정
		LocalDateTime deletionLimitDate = simulatedNow.minusDays(ACCOUNT_DELETION_DELAY_DAYS);

		// DEACTIVATED 또는 DELETED 상태 중 deletedAt 기준으로 90일 이상 지난 유저만 조회
		List<User> expiredUsers = userRepository.findByUserStatusInAndDeletedAtBefore(
			List.of(UserStatus.DEACTIVATED, UserStatus.DELETED),
			deletionLimitDate);

		for (User user : expiredUsers) {

			// soft delete 이후 90일 초과 유저 hardDelete
			userRepository.delete(user);
			log.info("[테스트 알림] 휴면 계정 90일 초과된 유저 삭제 - userId: {}, email: {}", user.getId(), user.getEmail());
		}
	}

	// TEST 관리자에 의해 정지된 계정 30일 후 자동 복구되는지 바로 확인하기 위한 테스트용 메서드
	@Transactional
	public void testRestoreSuspendedUsers(LocalDateTime simulatedNow) {

		// 30일 전 날짜를 기준으로 복구 시점 설정
		LocalDateTime reactivationTime = simulatedNow.minusDays(30);

		// SUSPENDED 상태이면서 deletedAt 이 30일 이상 된 유저
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
