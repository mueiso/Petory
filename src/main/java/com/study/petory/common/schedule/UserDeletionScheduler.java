package com.study.petory.common.schedule;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.study.petory.common.service.EmailService;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserDeletionScheduler {

	private final UserRepository userRepository;
	private final EmailService emailService;

	@Scheduled(cron = "0 0 2 * * ?")  // 매일 새벽 2시: 삭제 예정 알림
	@Transactional
	public void sendDeletionWarningEmails() {

		/*
		 * 1. 현재 시각 기준으로 계산 시작
		 * 2. 89일 전 시점 계산: 삭제된 지 '89일 전부터 85일 전까지'의 기간을 만들기 위한 계산 (-90+1로 계산하는 이유)
		 * 3. 85일 전 시점 계산
		 */
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime from = now.minusDays(90).plusDays(1);
		LocalDateTime to = now.minusDays(85);

		// 위 시간 범위 안에서 softDelete 처리된 사용자 목록 조회
		List<User> usersToBeDeleted = userRepository.findByDeletedAtBetween(from, to);

		for (User user : usersToBeDeleted) {
			String email = user.getEmail();
			String name = user.getUserPrivateInfo().getName();

			// 이메일 발송 + 삭제 예정일 함께 전달
			emailService.sendDeletionWarning(email, name, user.getDeletedAt());
			log.info("[알림] 삭제 예정 사용자에게 이메일 전송 - email: {}", email);
		}
	}

	@Scheduled(cron = "0 0 3 * * ?")  // 매일 새벽 3시: hardDelete 실행
	@Transactional
	public void hardDeleteExpiredUsers() {

		// 90일 전 날짜를 기준으로 삭제 시점 설정
		LocalDateTime deletionLimitDate = LocalDateTime.now().minusDays(90);

		// softDelete 된 시점이 90일보다 더 이전인 사용자들 조회 (= softDelete 된지 90일 초과된 유저 조회)
		List<User> expiredUsers = userRepository.findByDeletedAtBefore(deletionLimitDate);

		for (User user : expiredUsers) {

			// softDelete 이후 90일 초과 유저 hardDelete
			userRepository.delete(user);
			log.info("[하드삭제] 90일 초과 사용자 삭제 - userId: {}, email: {}", user.getId(), user.getEmail());
		}
	}
}
