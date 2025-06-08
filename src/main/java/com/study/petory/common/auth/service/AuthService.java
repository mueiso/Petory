package com.study.petory.common.auth.service;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.study.petory.common.auth.dto.TokenResponseDto;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.repository.UserRepository;
import com.study.petory.exception.CustomException;
import com.study.petory.exception.enums.ErrorCode;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserRepository userRepository;
	private final JwtProvider jwtProvider;
	private final StringRedisTemplate redisTemplate;

	/*
	 * Google OAuth2 로그인 성공 시 토큰 발급
	 */
	public TokenResponseDto issueToken(User user) {

		// 기존 유저 조회 또는 저장
		User savedUser = userRepository.findByEmail(user.getEmail())
			.orElseGet(() -> userRepository.save(user)); // 없으면 저장 후 반환

		if (savedUser.getId() == null) {
			throw new IllegalStateException("사용자 정보를 저장했지만 ID가 생성되지 않았습니다. DB 설정을 확인하세요.");
		}

		// Access Token: 사용자 정보를 포함해 짧은 생명주기로 발급
		String accessToken = jwtProvider.createAccessToken(savedUser.getId(), savedUser.getEmail(),
			savedUser.getNickname());

		// Refresh Token: ID만 기반으로 더 긴 생명주기로 발급
		String refreshToken = jwtProvider.createRefreshToken(savedUser.getId());

		// Redis 에 Refresh Token 을 {email}키로 저장
		jwtProvider.storeRefreshToken(user.getEmail(), refreshToken);

		return new TokenResponseDto(accessToken, refreshToken);
	}

	/*
	 * 로그아웃: Access Token 블랙리스트 처리 + Refresh Token 삭제
	 */
	public void logout(String accessToken, String email) {

		// 접두어 "Bearer " 제거 후 토큰 문자열만 추출
		String pureToken = jwtProvider.subStringToken(accessToken);

		// 토큰 만료 시간 계산
		long expiration = jwtProvider.getClaims(accessToken).getExpiration().getTime() - System.currentTimeMillis();

		// Redis 에 "BLACKLIST_{token}" 키로 남은 유효시간만큼 저장
		redisTemplate
			.opsForValue()
			.set("BLACKLIST_" + pureToken, "logout", expiration, TimeUnit.MILLISECONDS);

		// Refresh Token 삭제
		jwtProvider.deleteRefreshToken(email);
	}

	/*
	 * RefreshToken을 쿠키에서 읽고 AccessToken 재발급
	 */
	public TokenResponseDto reissue(HttpServletRequest request, String email) {

		// 쿠키에서 RefreshToken 추출
		String refreshToken = extractRefreshTokenFromCookie(request);

		// Redis 에 저장된 Refresh Token 과 일치하는지, 만료되지 않았는지 검증
		if (!jwtProvider.isValidRefreshToken(email, refreshToken)) {
			throw new CustomException(ErrorCode.INVALID_TOKEN);
		}

		User user = userRepository.findByEmail(email)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		// 새로운 AccessToken + RefreshToken 생성
		String newAccessToken = jwtProvider.createAccessToken(user.getId(), user.getEmail(), user.getNickname());
		String newRefreshToken = jwtProvider.createRefreshToken(user.getId());

		// 기존 RefreshToken 삭제 후 새 토큰 Redis 에 재저장
		jwtProvider.deleteRefreshToken(email);
		jwtProvider.storeRefreshToken(email, newRefreshToken);

		// refreshToken 은 HttpOnly 쿠키로 재설정 (Controller 책임)
		return new TokenResponseDto(newAccessToken, newRefreshToken); // refreshToken 은 Controller 에서 쿠키로 설정
	}

	// 쿠키에서 RefreshToken 추출
	private String extractRefreshTokenFromCookie(HttpServletRequest request) {

		if (request.getCookies() == null) {
			throw new CustomException(ErrorCode.INVALID_TOKEN);
		}

		for (Cookie cookie : request.getCookies()) {
			if ("refreshToken".equals(cookie.getName())) {
				return cookie.getValue();
			}
		}
		throw new CustomException(ErrorCode.INVALID_TOKEN);
	}
}