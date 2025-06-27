package com.study.petory.common.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.study.petory.domain.notification.entity.Notification;
import com.study.petory.domain.notification.repository.NotificationRepository;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NotificationScheduler {

	private final NotificationRepository notificationRepository;
	private final JobLauncher jobLauncher;
	private final Job sendDailyQuestionJob;

	// @Scheduled(cron = "0 0 0 * * *")
	// @Transactional
	// public void sendDailyQuestionNotification() {
	//
	// 	List<User> users = userRepository.findAll();
	//
	// 	for (User user : users) {
	//
	// 		Notification notification = Notification.builder()
	// 			.user(user)
	// 			.content("오늘의 질문이 도착했습니다 !")
	// 			.build();
	//
	// 		notificationRepository.save(notification);
	// 	}
	// }

	@Scheduled(cron = "0 0 0 * * *")
	public void sendDailyQuestionNotification() throws Exception{
		JobParameters jobParameters = new JobParametersBuilder()
			.addLong("time", System.currentTimeMillis())
			.toJobParameters();

		jobLauncher.run(sendDailyQuestionJob, jobParameters);
	}

	@Scheduled(cron = "0 0 21 * * *")
	@Transactional
	public void deleteNotificationMoreThan30days() {

		LocalDateTime duration = LocalDateTime.now().minusDays(30);

		notificationRepository.deleteByCreatedAtBefore(duration);
	}

}
