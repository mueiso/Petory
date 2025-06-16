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
public class UserCleanupScheduler {

	// private final UserRepository userRepository;
	// private final EmailService emailService;

	// @Scheduled(cron = "0 0 2 * * ?")  // 매일 새벽 2시: 삭제 예정 알림
	// @Transactional
	// public void notifyPendingDeletionUsers() {
	//
	// 	LocalDateTime now = LocalDateTime.now();
	// 	LocalDateTime from = now.minusDays(90).plusDays(1);  // 89일 전
	// 	LocalDateTime to = now.minusDays(85);                // 85일 전
	//
	// 	List<User> pendingUsers = userRepository.findByDeletedAtBetween(from, to);

	// 	for (User user : pendingUsers) {
	// 		String email = user.getEmail();
	// 		String name = user.getUserPrivateInfo().getName();
	//
	// 		log.info("[알림] 삭제 예정 사용자에게 이메일 전송 - email: {}", email);
	//
	// 		emailService.sendDeletionWarning(email, name, user.getDeletedAt());
	// 	}
	// }

}
