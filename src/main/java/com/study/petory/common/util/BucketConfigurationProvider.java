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

		return () -> BucketConfiguration.builder()
			.addLimit(limit -> limit.capacity(10).refillIntervally(10, Duration.ofHours(1)))
			.build();
	}

}
