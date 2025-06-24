// package com.study.petory.domain.user.service;
//
// import java.util.concurrent.TimeUnit;
//
// import org.springframework.data.redis.core.RedisTemplate;
// import org.springframework.stereotype.Service;
//
// import com.study.petory.common.exception.CustomException;
// import com.study.petory.common.exception.enums.ErrorCode;
// import com.study.petory.common.service.EmailService;
//
// import lombok.RequiredArgsConstructor;
//
// @Service
// @RequiredArgsConstructor
// public class EmailVerificationService {
//
// 	private final RedisTemplate<String, Object> redisCacheTemplate;
// 	private final EmailService emailService;
//
// 	private static final String REDIS_PREFIX = "EMAIL_VERIFY:";
// 	private static final long TTL_MINUTES = 10;
//
// 	// 인증 코드 발송
// 	public void sendVerificationCode(String email) {
//
// 		String code = generateCode();
// 		String key = getRedisKey(email);
//
// 		redisCacheTemplate.opsForValue().set(key, code, TTL_MINUTES, TimeUnit.MINUTES);
// 		emailService.sendVerificationEmailHtml(email, code);
// 	}
//
// 	// 인증 코드 검증
// 	public void verifyCode(String email, String inputCode) {
//
// 		String key = getRedisKey(email);
// 		Object savedCode = redisCacheTemplate.opsForValue().get(key);
//
// 		if (savedCode == null || !inputCode.equals(savedCode.toString())) {
// 			throw new CustomException(ErrorCode.INVALID_VERIFICATION_CODE);
// 		}
//
// 		// 인증 성공 시 Redis 에서 키 삭제
// 		redisCacheTemplate.delete(key);
// 	}
//
// 	private String getRedisKey(String email) {
//
// 		return REDIS_PREFIX + email;
// 	}
//
// 	private String generateCode() {
//
// 		return String.valueOf((int)(Math.random() * 900000) + 100000); // 6자리 숫자
// 	}
// }
