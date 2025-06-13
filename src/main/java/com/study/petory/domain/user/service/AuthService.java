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
import com.study.petory.domain.user.entity.Role;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.entity.UserRole;
import com.study.petory.domain.user.repository.UserRepository;

import jakarta.transaction.Transactional;

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
			.map(userRole -> "ROLE_" + userRole.getRole().name())
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
	 * AccessToken 을 블랙리스트 등록 - 만료시간되면 자동 삭제
	 * Redis 에 저장된 RefreshToken 제거
	 */
	public void logout(String accessToken) {

		String pureToken = jwtProvider.subStringToken(accessToken);
		Long userId = Long.valueOf(jwtProvider.getClaims(accessToken).getSubject());

		long expiration = jwtProvider.getClaims(accessToken).getExpiration().getTime() - System.currentTimeMillis();

		/*
		 * AccessToken 을 블랙리스트에 등록하는 로직
		 * expiration 시간은 AccessToken 의 남은 유효기간만큼 설정되어, 만료 시 자동으로 삭제
		 */
		loginRefreshToken.opsForValue()
			.set("BLACKLIST_" + pureToken, "logout", expiration, TimeUnit.MILLISECONDS);

		jwtProvider.deleteRefreshToken(userId);
	}

	/*
	 * [토큰 재발급]
	 * AccessToken 이 만료된 경우에만,
	 * 전달된 refreshToken 기반으로 AccessToken 재발급
	 */
	public TokenResponseDto reissue(String accessToken, String refreshTokenRaw) {

		// 1. AccessToken 만료 여부 확인
		if (!jwtProvider.isAccessTokenExpired(accessToken)) {
			throw new CustomException(ErrorCode.TOKEN_NOT_EXPIRED);
		}

		// 2. Bearer 접두사 제거 (있는 경우만)
		String refreshToken;
		if (refreshTokenRaw.startsWith("Bearer ")) {
			refreshToken = jwtProvider.subStringToken(refreshTokenRaw);
		} else {
			refreshToken = refreshTokenRaw;
		}

		// 3. userId 추출
		Long userId = Long.valueOf(jwtProvider.getClaims(refreshToken).getSubject());

		// 4. Redis RefreshToken 검증
		if (!jwtProvider.isValidRefreshToken(userId, refreshToken)) {
			throw new CustomException(ErrorCode.INVALID_TOKEN);
		}

		// 5. 사용자 조회
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		// 6. 역할 목록 추출
		List<String> roles = user.getUserRole().stream()
			.map(userRole -> "ROLE_" + userRole.getRole().name())
			.toList();

		// 7. 새 토큰 발급
		String newAccessToken = jwtProvider.createAccessToken(
			user.getId(), user.getEmail(), user.getNickname(), roles
		);
		String newRefreshToken = jwtProvider.createRefreshToken(user.getId());

		// 8. Redis 저장 갱신
		jwtProvider.deleteRefreshToken(user.getId());
		jwtProvider.storeRefreshToken(user.getId(), newRefreshToken);

		return new TokenResponseDto(newAccessToken, newRefreshToken);
	}

	@Transactional
	public List<Role> addRoleToUser(Long userId, Role newRole) {

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		// 객체지향 방식으로 중복 Role 체크
		boolean alreadyHasSameRole = user.getUserRole().stream()
			.anyMatch(userRole -> userRole.isEqualRole(newRole));

		if (!alreadyHasSameRole) {
			throw new CustomException(ErrorCode.ALREADY_HAS_SAME_ROLE);
		}

		// 새 권한 추가
		user.getUserRole().add(UserRole.builder().role(newRole).build());

		// 유저가 가진 권한 목록 반환
		return user.getUserRole().stream()
			.map(UserRole::getRole)
			.toList();
	}

	@Transactional
	public void removeRoleFromUser(Long userId, Role roleToRemove) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		// 해당 Role 이 존재하는지 확인
		boolean hasRole = user.getUserRole().stream()
			.anyMatch(userRole -> userRole.getRole().equals(roleToRemove));

		if (!hasRole) {
			throw new CustomException(ErrorCode.ROLE_NOT_FOUND);
		}

		// Role 제거
		user.getUserRole().removeIf(userRole -> userRole.getRole().equals(roleToRemove));
	}
}
