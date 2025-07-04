package com.study.petory.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
@EnableScheduling
public class SchedulerConfig implements SchedulingConfigurer {

	private static final int POOL_SIZE = 4;

	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {

		ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();

		threadPoolTaskScheduler.setPoolSize(POOL_SIZE);
		threadPoolTaskScheduler.setThreadNamePrefix("scheduler-");
		threadPoolTaskScheduler.initialize();

		//설정한 스레드 풀 등록 -> @Scheduler 어노테이션 사용시 해당 스레드 풀을 이용하게 됨
		taskRegistrar.setTaskScheduler(threadPoolTaskScheduler);
	}
}
