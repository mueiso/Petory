package com.study.petory.common.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.entity.UserStatus;
import com.study.petory.domain.user.repository.UserRepository;
import com.study.petory.domain.user.service.UserDeletionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class UserSchedulerService {

	private static final int ACCOUNT_DELAY_DAYS = 90;  // 유예 기간

	private final EmailService emailService;
	private final UserRepository userRepository;
	private final UserDeletionService userDeletionService;

	// 미접속 유저에게 휴면 알림 이메일 발송 - 매일 자정
	public void sendDeactivationWarningEmails() {

		testSendDeactivationWarningEmails(getNow());
	}

	// 미접속 유저의 계정 휴면처리 - 매일 새벽 1시
	public void deactivateInactiveUsers() {

		testDeactivateInactiveUsers(getNow());
	}

	// 삭제 예정 유저에게 삭제 알림 이메일 발송 - 매일 새벽 2시
	public void sendDeletionWarningEmails() {

		testSendDeletionWarningEmails(getNow());
	}

	// 삭제 대상 유저 hard delete - 매일 새벽 3시
	public void hardDeleteExpiredUsers() {

		testHardDeleteExpiredUsers(getNow());
	}

	// 계정 정지 유저 복구 - 매일 새벽 4시
	public void restoreSuspendedUsers() {

		testRestoreSuspendedUsers(getNow());
	}

	// TEST 스테줄러에 맞춰 이메일 자동 발송 되는지 바로 확인하기 위한 테스트용 메서드
	public void testSendDeactivationWarningEmails(LocalDateTime simulatedNow) {

		// 85일 전 날짜를 기준으로 휴면 안내 메일 발송 시점 설정
		LocalDateTime deactivationEmailDate = simulatedNow.minusDays(85);

		// ACTIVE 상태이면서 85일 이상 updatedAt 의 변화가 없는 유저
		List<User> deactivationCandidates = userRepository.findByUserStatusAndUpdatedAtBefore(
			UserStatus.ACTIVE,
			deactivationEmailDate);

		for (User user : deactivationCandidates) {
			String email = user.getEmail();
			String name = user.getUserPrivateInfo().getName();

			// 이메일 발송 + 휴면 전환 예정일 함께 전달
			emailService.sendDeactivationWarning(email, name, deactivationEmailDate);
			log.info("[알림] 휴면 예정 유저에게 이메일 전송 - email: {}", email);
		}
	}

	// TEST 90알간 미접속 시 자동 휴면 계정으로 전환되는지 바로 확인하기 위한 테스트용 메서드
	public void testDeactivateInactiveUsers(LocalDateTime simulatedNow) {

		// 90일전 날짜를 기준으로 비활성화 시점 설정 (= 90일간 미접속 시)
		LocalDateTime inactivationDate = simulatedNow.minusDays(ACCOUNT_DELAY_DAYS);

		// ACTIVE 상태이면서 90일 이상 updatedAt 의 변화가 없는 유저
		List<User> deactivationCandidates = userRepository.findByUserStatusAndUpdatedAtBefore(
			UserStatus.ACTIVE,
			inactivationDate);

		for (User user : deactivationCandidates) {
			user.deactivateEntity();
			user.updateStatus(UserStatus.DEACTIVATED);

			log.info("[알림] 90일 미접속 유저 휴면처리 - userId: {}, email: {}", user.getId(), user.getEmail());
		}
	}

	// TEST 스테줄러에 맞춰 이메일 자동 발송 되는지 바로 확인하기 위한 테스트용 메서드
	public void testSendDeletionWarningEmails(LocalDateTime simulatedNow) {

		/*
		 * 1. 현재 시각 기준으로 계산 시작
		 * 2. 89일 전 시점 계산: soft delete '85일 ~ 89일' 사이의 기간을 만들기 위한 계산 (-90+1로 계산하는 이유)
		 * 3. 85일 전 시점 계산
		 */
		LocalDateTime from = simulatedNow.minusDays(ACCOUNT_DELAY_DAYS).plusDays(1);
		LocalDateTime to = simulatedNow.minusDays(85);

		// 위 시간 범위 안에서 soft delete 처리된 유저 목록 조회
		List<User> usersToBeDeleted = userRepository.findByUserStatusAndDeletedAtBetween(UserStatus.DEACTIVATED, from,
			to);

		for (User user : usersToBeDeleted) {
			String email = user.getEmail();
			String name = user.getUserPrivateInfo().getName();

			// 이메일 발송 + 삭제 예정일 함께 전달
			emailService.sendDeletionWarning(email, name, user.getDeletedAt());
			log.info("[알림] 삭제 예정 유저에게 이메일 전송 - email: {}", email);
		}
	}

	// TEST 스케줄러에 맞춰 휴면 계쩡 or 탈퇴 계정이 된 지 90일 초과된 유저 자동 hardDelete 되는지 바로 확인하기 위한 테스트용 메서드
	public void testHardDeleteExpiredUsers(LocalDateTime simulatedNow) {

		// 90일 전 날짜를 기준으로 삭제 시점 설정
		LocalDateTime deletionLimitDate = simulatedNow.minusDays(ACCOUNT_DELAY_DAYS);

		// DEACTIVATED 또는 DELETED 상태 중 deletedAt 기준으로 90일 이상 지난 유저만 조회
		List<User> expiredUsers = userRepository.findByUserStatusInAndDeletedAtBefore(
			List.of(UserStatus.DEACTIVATED, UserStatus.DELETED),
			deletionLimitDate);

		for (User user : expiredUsers) {

			// 참조 관계 끊고 유저 hard delete
			userDeletionService.deleteUser(user);
			log.info("[알림] deletedAt 90일 초과된 유저 삭제 - userId: {}, email: {}", user.getId(), user.getEmail());
		}
	}

	// TEST 관리자에 의해 정지된 계정 30일 후 자동 복구되는지 바로 확인하기 위한 테스트용 메서드
	public void testRestoreSuspendedUsers(LocalDateTime simulatedNow) {

		// 30일 전 날짜를 기준으로 복구 시점 설정
		LocalDateTime reactivationDate = simulatedNow.minusDays(30);

		// SUSPENDED 상태이면서 deletedAt 이 30일 이상 된 유저
		List<User> suspendedUsers = userRepository.findByUserStatusAndDeletedAtBefore(
			UserStatus.SUSPENDED,
			reactivationDate);

		for (User user : suspendedUsers) {
			user.restoreEntity();
			user.updateStatus(UserStatus.ACTIVE);

			log.info("[알림] 30일 정지됐던 계정 복구 - userId: {}, email: {}", user.getId(), user.getEmail());
		}
	}

	private LocalDateTime getNow() {

		return LocalDateTime.now();
	}
}
