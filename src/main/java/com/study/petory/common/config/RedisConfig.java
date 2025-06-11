package com.study.petory.common.config;

import java.time.Duration;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
@EnableCaching
public class RedisConfig {

	// 전역 캐시 설정
	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		RedisStandaloneConfiguration config = new RedisStandaloneConfiguration("localhost", 6381);
		config.setDatabase(0);
		return new LettuceConnectionFactory(config);
	}

	private RedisSerializer<Object> valueSerializer() {
		return new GenericJackson2JsonRedisSerializer();
	}

	@Bean
	public CacheManager cacheManager() {
		RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
			.entryTtl(Duration.ofDays(1))
			.serializeValuesWith(
				RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer())
			);
		return RedisCacheManager.builder(redisConnectionFactory())
			.cacheDefaults(config)
			.build();
	}

	// X하정님의 요청입니다 삭제하지 말아주세요 ㅠ

	// @Bean
	// public RedisTemplate<String, String> loginRefreshToken() {
	// 	RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
	// 	redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<Object>(Object.class));
	// 	redisTemplate.setConnectionFactory(RedisConnectionFactory 아무거나);
	// 	return redisTemplate;
	// }
}
