package com.study.petory.common.config;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.study.petory.domain.notification.entity.Notification;
import com.study.petory.domain.user.entity.User;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableBatchProcessing
public class NotificationBatchConfig {

	private final DataSource dataSource;

	private static final int CHUNK_SIZE = 500;

	@Bean
	@StepScope
	public JdbcCursorItemReader<User> userReader() {
		JdbcCursorItemReader<User> reader = new JdbcCursorItemReader<>();
		reader.setDataSource(dataSource);
		reader.setSql("SELECT id FROM tb_user ORDER BY id");
		reader.setRowMapper((rs, rowNum) -> new User(rs.getLong("id")));
		reader.setVerifyCursorPosition(false);
		return reader;
	}

	@Bean
	@StepScope
	public ItemProcessor<User, Notification> dailyQuestionProcessor() {
		return user -> Notification.builder()
			.user(user)
			.content("오늘의 질문이 도착했습니다 !")
			.build();
	}

	@Bean
	@StepScope
	public JdbcBatchItemWriter<Notification> dailyQuestionWriter() {
		return new JdbcBatchItemWriterBuilder<Notification>()
			.dataSource(dataSource)
			.sql("INSERT INTO tb_notification (user_id, content, created_at) VALUES (?, ?, ?)")
			.itemPreparedStatementSetter((notification, ps) -> {
				ps.setLong(1, notification.getUserId());
				ps.setString(2, notification.getContent());
				ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
			})
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
		JdbcCursorItemReader<User> userReader,
		ItemProcessor<User, Notification> itemProcessor,
		JdbcBatchItemWriter<Notification> itemWriter
	) {
		return new StepBuilder("sendDailyQuestionStep", jobRepository)
			.<User, Notification>chunk(CHUNK_SIZE, transactionManager)
			.reader(userReader)
			.processor(itemProcessor)
			.writer(itemWriter)
			.build();
	}
}
