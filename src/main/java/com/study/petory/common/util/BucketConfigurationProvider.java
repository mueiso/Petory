package com.study.petory.common.util;

import java.time.Duration;
import java.util.function.Supplier;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.bucket4j.BucketConfiguration;

@Configuration
public class BucketConfigurationProvider {

	@Bean
	public Supplier<BucketConfiguration> createBucketConfig() {

		// 버킷 설정 Greedy 방식
		// 1시간에 최대 10개의 토큰 사용가능
		// 토큰은 1시간 주기로 10개의 토큰을 한번에 채워줌
		return () -> BucketConfiguration.builder()
			.addLimit(limit -> limit.capacity(10).refillGreedy(10, Duration.ofHours(1)))
			.build();
	}

}
