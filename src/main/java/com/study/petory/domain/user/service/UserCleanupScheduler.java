// package com.study.petory.domain.user.service;
//
// import java.time.LocalDateTime;
//
// import org.springframework.scheduling.annotation.Scheduled;
// import org.springframework.stereotype.Component;
//
// import com.study.petory.domain.user.repository.UserRepository;
//
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
//
// @Slf4j
// @Component
// @RequiredArgsConstructor
// public class UserCleanupScheduler {
//
// 	private final UserRepository userRepository;
// 	private final EmailService emailService; // 가정: 이메일 발송 로직
//
// 	@Scheduled(cron = "0 0 2 * * ?") // 매일 새벽 2시: 삭제 예정 알림
// 	@Transactional
// 	public void notifyPendingDeletionUsers() {
// 		LocalDateTime now = LocalDateTime.now();
// 		LocalDateTime from = now.minusDays(90).plusDays(1); // 89일 전
// 		LocalDateTime to = now.minusDays(85);               // 85일 전
//
// 		List<User> pendingUsers = userRepository.findByDeletedAtBetween(from, to);
//
// 		for (User user : pendingUsers) {
// 			String email = user.getEmail();
// 			String name = user.getUserPrivateInfo().getName();
//
// 			log.info("[알림] 삭제 예정 사용자에게 이메일 전송 - email: {}", email);
//
// 			emailService.sendDeletionWarning(email, name, user.getDeletedAt());
// 		}
// 	}
//
// 	@Scheduled(cron = "0 0 3 * * ?") // 매일 새벽 3시: 하드 삭제 실행
// 	@Transactional
// 	public void hardDeleteExpiredUsers() {
// 		LocalDateTime cutoff = LocalDateTime.now().minusDays(90);
// 		List<User> expiredUsers = userRepository.findByDeletedAtBefore(cutoff);
//
// 		for (User user : expiredUsers) {
// 			log.info("[하드삭제] 90일 초과 사용자 삭제 - userId: {}, email: {}", user.getId(), user.getEmail());
// 			userRepository.delete(user);
// 		}
// 	}
// }
