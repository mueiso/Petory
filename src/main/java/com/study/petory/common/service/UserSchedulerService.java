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

	private static final int ACCOUNT_DELAY_DAYS = 90;

	private final EmailService emailService;
	private final UserRepository userRepository;
	private final UserDeletionService userDeletionService;

	// 미접속 유저에게 휴면 알림 이메일 발송 - 매일 자정
	public void sendDeactivationWarningEmails() {

		NowSendDeactivationWarningEmails(getNow());
	}

	// 미접속 유저의 계정 휴면처리 - 매일 새벽 1시
	public void deactivateInactiveUsers() {

		NowDeactivateInactiveUsers(getNow());
	}

	// 삭제 예정 유저에게 삭제 알림 이메일 발송 - 매일 새벽 2시
	public void sendDeletionWarningEmails() {

		NowSendDeletionWarningEmails(getNow());
	}

	// 삭제 대상 유저 hard delete - 매일 새벽 3시
	public void hardDeleteExpiredUsers() {

		NowHardDeleteExpiredUsers(getNow());
	}

	// 계정 정지 유저 복구 - 매일 새벽 4시
	public void restoreSuspendedUsers() {

		NowRestoreSuspendedUsers(getNow());
	}

	// 스테줄러에 맞춰 이메일 자동 발송 되는지 바로 확인하기 위한 메서드
	public void NowSendDeactivationWarningEmails(LocalDateTime simulatedNow) {

		LocalDateTime deactivationEmailDate = simulatedNow.minusDays(85);

		List<User> deactivationCandidates = userRepository.findByUserStatusAndUpdatedAtBefore(
			UserStatus.ACTIVE,
			deactivationEmailDate);

		for (User user : deactivationCandidates) {
			String email = user.getEmail();
			String name = user.getUserPrivateInfo().getName();

			emailService.sendDeactivationWarning(email, name, deactivationEmailDate);
			log.info("[알림] 휴면 예정 유저에게 이메일 전송 - email: {}", email);
		}
	}

	// 90알간 미접속 시 자동 휴면 계정으로 전환되는지 바로 확인하기 위한 메서드
	public void NowDeactivateInactiveUsers(LocalDateTime simulatedNow) {

		LocalDateTime inactivationDate = simulatedNow.minusDays(ACCOUNT_DELAY_DAYS);

		List<User> deactivationCandidates = userRepository.findByUserStatusAndUpdatedAtBefore(
			UserStatus.ACTIVE,
			inactivationDate);

		for (User user : deactivationCandidates) {
			user.deactivateEntity();
			user.updateStatus(UserStatus.DEACTIVATED);

			log.info("[알림] 90일 미접속 유저 휴면처리 - userId: {}, email: {}", user.getId(), user.getEmail());
		}
	}

	// 스테줄러에 맞춰 이메일 자동 발송 되는지 바로 확인하기 위한 메서드
	public void NowSendDeletionWarningEmails(LocalDateTime simulatedNow) {

		LocalDateTime from = simulatedNow.minusDays(ACCOUNT_DELAY_DAYS).plusDays(1);
		LocalDateTime to = simulatedNow.minusDays(85);

		List<User> usersToBeDeleted = userRepository.findByUserStatusAndDeletedAtBetween(UserStatus.DEACTIVATED, from,
			to);

		for (User user : usersToBeDeleted) {
			String email = user.getEmail();
			String name = user.getUserPrivateInfo().getName();

			emailService.sendDeletionWarning(email, name, user.getDeletedAt());
			log.info("[알림] 삭제 예정 유저에게 이메일 전송 - email: {}", email);
		}
	}

	// 스케줄러에 맞춰 휴면 계쩡 or 탈퇴 계정이 된 지 90일 초과된 유저 자동 hardDelete 되는지 바로 확인하기 위한 메서드
	public void NowHardDeleteExpiredUsers(LocalDateTime simulatedNow) {

		LocalDateTime deletionLimitDate = simulatedNow.minusDays(ACCOUNT_DELAY_DAYS);

		List<User> expiredUsers = userRepository.findByUserStatusInAndDeletedAtBefore(
			List.of(UserStatus.DEACTIVATED, UserStatus.DELETED),
			deletionLimitDate);

		for (User user : expiredUsers) {

			userDeletionService.deleteUser(user);
			log.info("[알림] deletedAt 90일 초과된 유저 삭제 - userId: {}, email: {}", user.getId(), user.getEmail());
		}
	}

	// 관리자에 의해 정지된 계정 30일 후 자동 복구되는지 바로 확인하기 위한 메서드
	public void NowRestoreSuspendedUsers(LocalDateTime simulatedNow) {

		LocalDateTime reactivationDate = simulatedNow.minusDays(30);

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
