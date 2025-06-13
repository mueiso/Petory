package com.study.petory.domain.user.service;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.study.petory.common.exception.CustomException;
import com.study.petory.common.exception.enums.ErrorCode;
import com.study.petory.common.security.JwtProvider;
import com.study.petory.domain.user.dto.TokenResponseDto;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.repository.UserRepository;

@Service
public class AuthService {

	private final UserRepository userRepository;
	private final JwtProvider jwtProvider;
	private final RedisTemplate<String, String> loginRefreshToken;

	public AuthService(
		UserRepository userRepository,
		JwtProvider jwtProvider,
		@Qualifier("loginRefreshToken")
		RedisTemplate<String, String> loginRefreshToken) {
		this.userRepository = userRepository;
		this.jwtProvider = jwtProvider;
		this.loginRefreshToken = loginRefreshToken;
	}

	/*
	 * [토큰 발급]
	 * Google OAuth2 로그인 성공 시 토큰 발급
	 * Redis 에 refreshToken 저장 (userId 기준 키)
	 */
	public TokenResponseDto issueToken(User user) {

		User savedUser = userRepository.findByEmail(user.getEmail())
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		if (savedUser.getId() == null) {
			throw new CustomException(ErrorCode.USER_ID_NOT_GENERATED);
		}

		List<String> roles = savedUser.getUserRole().stream()
			.map(userRole -> "Role" + userRole.getRole().name())
			.toList();

		String accessToken = jwtProvider.createAccessToken(
			savedUser.getId(),
			savedUser.getEmail(),
			savedUser.getNickname(),
			roles  // JWT Claim 에 포함된 권한 리스트
		);

		String refreshToken = jwtProvider.createRefreshToken(savedUser.getId());

		jwtProvider.storeRefreshToken(savedUser.getId(), refreshToken);

		return new TokenResponseDto(accessToken, refreshToken);
	}

	/*
	 * [로그아웃 처리]
	 * AccessToken 을 블랙리스트 등록
	 * Redis 에 저장된 RefreshToken 제거
	 */
	public void logout(String accessToken) {

		String pureToken = jwtProvider.subStringToken(accessToken);
		Long userId = Long.valueOf(jwtProvider.getClaims(accessToken).getSubject());

		long expiration = jwtProvider.getClaims(accessToken).getExpiration().getTime() - System.currentTimeMillis();

		loginRefreshToken.opsForValue()
			.set("BLACKLIST_" + pureToken, "logout", expiration, TimeUnit.MILLISECONDS);

		jwtProvider.deleteRefreshToken(userId);
	}

	/*
	 * [토큰 재발급]
	 * Authorization 헤더로 전달된 refreshToken 기반으로 AccessToken 재발급
	 * Redis 의 refreshToken 과 일치하는지 검증
	 */
	public TokenResponseDto reissue(String bearerRefreshToken) {

		// Bearer 접두어 제거
		String refreshToken = jwtProvider.subStringToken(bearerRefreshToken);

		// JWT Claims 에서 userId 추출
		Long userId = Long.valueOf(jwtProvider.getClaims(refreshToken).getSubject());

		// Redis 에 저장된 RefreshToken 과 비교
		if (!jwtProvider.isValidRefreshToken(userId, refreshToken)) {
			throw new CustomException(ErrorCode.INVALID_TOKEN);
		}

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		List<String> roles = user.getUserRole().stream()
			.map(userRole -> "Role" + userRole.getRole().name())
			.toList();

		String newAccessToken = jwtProvider.createAccessToken(
			user.getId(),
			user.getEmail(),
			user.getNickname(),
			roles
			);

		String newRefreshToken = jwtProvider.createRefreshToken(user.getId());

		// Redis 에 기존 RefreshToken 삭제 및 신규 저장
		jwtProvider.deleteRefreshToken(user.getId());
		jwtProvider.storeRefreshToken(user.getId(), newRefreshToken);

		return new TokenResponseDto(newAccessToken, newRefreshToken);
	}
}
