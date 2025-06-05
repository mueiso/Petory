package com.study.petory.common.auth.service;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.study.petory.common.auth.dto.TokenResponseDto;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserRepository userRepository;
	private final JwtProvider jwtProvider;
	private final StringRedisTemplate redisTemplate;

	// Google OAuth2 로그인 성공 시 토큰 발급
	public TokenResponseDto issueToken(User user) {

		String accessToken = jwtProvider.createAccessToken(user.getId(), user.getEmail(), user.getNickname());
		String refreshToken = jwtProvider.createRefreshToken(user.getId());

		// Redis 에 Refresh Token 저장
		jwtProvider.storeRefreshToken(user.getEmail(), refreshToken);

		return new TokenResponseDto(accessToken, refreshToken);
	}

	// 로그아웃 - Access Token 블랙리스트 처리 + Refresh Token 삭제
	public void logout(String accessToken, String email) {

		String pureToken = jwtProvider.subStringToken(accessToken);
		long expiration = jwtProvider.getClaims(accessToken).getExpiration().getTime() - System.currentTimeMillis();

		// Access Token 블랙리스트 등록
		redisTemplate
			.opsForValue()
			.set("BLACKLIST_" + pureToken, "logout", expiration, TimeUnit.MILLISECONDS);

		// Refresh Token 삭제
		jwtProvider.deleteRefreshToken(email);
	}

	// Refresh Token 을 이용한 Access Token 재발급
	public TokenResponseDto reissue(String email, String refreshToken) {

		if (!jwtProvider.isValidRefreshToken(email, refreshToken)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh Token이 유효하지 않습니다.");
		}

		User user = userRepository.findByEmail(email)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "사용자 정보를 찾을 수 없습니다."));

		String newAccessToken = jwtProvider.createAccessToken(user.getId(), user.getEmail(), user.getNickname());
		String newRefreshToken = jwtProvider.createRefreshToken(user.getId());

		jwtProvider.deleteRefreshToken(email);
		jwtProvider.storeRefreshToken(email, newRefreshToken);

		return new TokenResponseDto(newAccessToken, newRefreshToken);
	}
}