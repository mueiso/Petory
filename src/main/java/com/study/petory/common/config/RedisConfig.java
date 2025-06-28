package com.study.petory.common.config;

import static com.study.petory.common.util.CustomDateUtil.*;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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

	@Bean
	@Primary
	public RedisConnectionFactory redisConnectionManager() {
		RedisStandaloneConfiguration config = new RedisStandaloneConfiguration("localhost", 6381);
		config.setDatabase(0);
		return new LettuceConnectionFactory(config);
	}

	// 전역 캐시 설정
	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		RedisStandaloneConfiguration config = new RedisStandaloneConfiguration("localhost", 6381);
		config.setDatabase(1);
		return new LettuceConnectionFactory(config);
	}

	@Bean(name = "redisCacheTemplate")
	public RedisTemplate<String, Object> redisCacheTemplate() {
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(redisConnectionFactory());
		redisTemplate.setValueSerializer(valueSerializer());
		return redisTemplate;
	}

	private RedisSerializer<Object> valueSerializer() {
		return new GenericJackson2JsonRedisSerializer();
	}

	@Bean
	public CacheManager cacheManager() {
		RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
			.entryTtl(remainderTime())
			.serializeValuesWith(
				RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer())
			);
		return RedisCacheManager.builder(redisConnectionFactory())
			.cacheDefaults(config)
			.build();
	}

	@Bean(name = "loginRefreshToken")
	public RedisTemplate<String, String> loginRefreshToken() {
		RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
		redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<Object>(Object.class));
		redisTemplate.setConnectionFactory(redisConnectionManager());
		return redisTemplate;
	}
}
