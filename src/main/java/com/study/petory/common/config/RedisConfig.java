package com.study.petory.common.config;

import static com.study.petory.common.util.DateUtil.*;

import java.time.Duration;

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

import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableCaching
@Slf4j
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

	// bucket4j + redis
	@Bean
	public RedisClient redisClientBucket4j() {
		return RedisClient.create("redis://localhost:6381/2");
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

	/**
	 * Reference
	 * https://bucket4j.com/8.9.0/toc.html#bucket4j-redis
	 */
	@Bean
	public StatefulRedisConnection<String, byte[]> redisBucket4jConnection(RedisClient redisClientBucket4j) {
		return redisClientBucket4j
			.connect(RedisCodec.of(StringCodec.UTF8, ByteArrayCodec.INSTANCE)); // 키는 UTF-8 문자로, 값은 바이트 배열로 직렬화
	}

	// proxy manager 가 redis 사용한다.
	// LettuceBasedProxyManager 는 비동기 고성능 처리 지원
	@Bean
	public ProxyManager<String> lettuceBasedProxyManager(
		StatefulRedisConnection<String, byte[]> redisBucket4jConnection) {

		// Expiration 전략 설정
		return LettuceBasedProxyManager.builderFor(redisBucket4jConnection)
			.withExpirationStrategy(
				ExpirationAfterWriteStrategy.fixedTimeToLive(Duration.ofHours(1)))
			.build();
	}
}
