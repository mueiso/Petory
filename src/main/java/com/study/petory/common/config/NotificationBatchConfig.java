package com.study.petory.common.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.study.petory.domain.notification.entity.Notification;
import com.study.petory.domain.user.entity.User;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class NotificationBatchConfig {

	private final EntityManagerFactory entityManagerFactory;

	private final int CHUNK_SIZE = 100;

	@Bean
	public JpaPagingItemReader<User> userReader(
		JobRepository jobRepository,
		PlatformTransactionManager transactionManager
	) {
		return new JpaPagingItemReaderBuilder<User>()
			.name("userReader")
			.entityManagerFactory(entityManagerFactory)
			.queryString("SELECT u FROM User u")
			.pageSize(CHUNK_SIZE)
			.build();

	}

	@Bean
	public ItemProcessor<User, Notification> dailyQuestionProcessor() {
		return user -> Notification.builder()
			.user(user)
			.content("오늘의 질문이 도착했습니다 !")
			.build();
	}

	@Bean
	public JpaItemWriter<Notification> dailyQuestionWriter() {
		return new JpaItemWriterBuilder<Notification>()
			.entityManagerFactory(entityManagerFactory)
			.build();
	}

	@Bean("sendDailyQnaJob")
	public Job sendDailyQuestionJob(
		JobRepository jobRepository,
		Step sendDailyQuestionStep
	) {
		return new JobBuilder("sendDailyQuestionJob", jobRepository)
			.start(sendDailyQuestionStep)
			.build();
	}

	@Bean
	public Step sendDailyQuestionStep(
		JobRepository jobRepository,
		PlatformTransactionManager transactionManager,
		JpaPagingItemReader<User> userReader,
		ItemProcessor<User, Notification> itemProcessor,
		JpaItemWriter<Notification> itemWriter
	) {
		return new StepBuilder("sendDailyQuestionStep", jobRepository)
			.<User, Notification>chunk(CHUNK_SIZE, transactionManager)
			.reader(userReader)
			.processor(itemProcessor)
			.writer(itemWriter)
			.build();
	}
}
