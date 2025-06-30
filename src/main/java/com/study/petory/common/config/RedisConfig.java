package com.study.petory.common.config;

import static com.study.petory.common.util.DateUtil.*;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
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

	@Value("${data.redis.host}")
	private String hostName;

	@Value("${data.redis.port}")
	private int port;

	private static final String REDIS_PREFIX = "redis://";

	// 전역 캐시 설정
	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(hostName, port);
		config.setDatabase(0);
		return new LettuceConnectionFactory(config);
	}

	// bucket4j + redis
	@Bean
	public RedisClient redisClientBucket4j() {
		return RedisClient.create(REDIS_PREFIX + hostName + ":" + port);
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
		redisTemplate.setConnectionFactory(redisConnectionFactory());
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

	// proxy manager 는 key를 기준으로 Bucket 객체를 Redis가 사용할 수 있게 해줌
	// LettuceBasedProxyManager 는 Lettuce 클라이언트를 사용하는 비동기 고성능 구현체
	@Bean
	public ProxyManager<String> lettuceBasedProxyManager(
		StatefulRedisConnection<String, byte[]> redisBucket4jConnection) {

		// Expiration 전략 설정(Redis에 저장되는 Bucket의 TTL 전략 설정)
		// 해당 Bucket이 Redis에 쓰여진 이후 고정 1시간 동안 살아있게 설정
		// 버킷 사용여부를 떠나 1시간이 지나면 자동 삭제
		return LettuceBasedProxyManager.builderFor(redisBucket4jConnection)
			.withExpirationStrategy(
				ExpirationAfterWriteStrategy.fixedTimeToLive(Duration.ofHours(1)))
			.build();
	}
}
