package com.study.petory.domain.user.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.study.petory.common.exception.CustomException;
import com.study.petory.common.exception.enums.ErrorCode;
import com.study.petory.common.security.JwtProvider;
import com.study.petory.domain.user.dto.TokenResponseDto;
import com.study.petory.domain.user.dto.UpdateUserRequestDto;
import com.study.petory.domain.user.dto.UserProfileResponseDto;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.entity.UserPrivateInfo;
import com.study.petory.domain.user.entity.UserStatus;
import com.study.petory.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final JwtProvider jwtProvider;

	/*
	 * [테스트 전용 - 로그인]
	 * userId를 기준으로 로그인
	 * 비활성화된 유저는 로그인 불가 예외 발생
	 */
	@Override
	@Transactional
	public TokenResponseDto testLogin(Long userId) {

		User user = findUserById(userId);

		if (user.getUserStatus() != UserStatus.ACTIVE
			&& user.getUserStatus() != UserStatus.DEACTIVATED) {
			throw new CustomException(ErrorCode.LOGIN_UNAVAILABLE);
		}

		List<String> roles = user.getUserRole().stream()
			.map(userRole -> "ROLE_" + userRole.getRole().name())
			.toList();

		String accessToken = jwtProvider.createAccessToken(
			user.getId(),
			user.getEmail(),
			user.getNickname(),
			roles
		);

		String refreshToken = jwtProvider.createRefreshToken(user.getId());
		jwtProvider.storeRefreshToken(user.getId(), refreshToken);

		return new TokenResponseDto(accessToken, refreshToken);
	}

	// 현재 사용자 정보 조회
	@Override
	@Transactional(readOnly = true)
	public UserProfileResponseDto findMyProfile(String email) {

		User user = userRepository.findByEmail(email)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		// UserPrivateInfo 조회
		UserPrivateInfo userPrivateInfo = user.getUserPrivateInfo();

		return new UserProfileResponseDto(
			user.getEmail(),
			userPrivateInfo.getName(),
			user.getNickname(),
			userPrivateInfo.getMobileNum()
		);
	}

	// 사용자 정보 업데이트
	@Override
	@Transactional
	public void updateProfile(String email, UpdateUserRequestDto dto) {

		User user = userRepository.findByEmail(email)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		// 닉네임 수정
		user.updateNickname(dto.getNickname());

		// UserPrivateInfo 수정
		user.getUserPrivateInfo().update(dto.getMobileNum());
	}

	// 사용자 탈퇴 (soft delete 처리)
	@Override
	@Transactional
	public void deleteAccount(String email) {

		User user = userRepository.findByEmail(email)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		user.deactivateEntity();
		user.updateStatus(UserStatus.DELETED);
	}

	@Override
	@Transactional(readOnly = true)
	public User findUserById(Long userId) {

		return userRepository.findUserById(userId)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
	}

	@Override
	@Transactional
	public User findUserByEmail(String email) {

		return userRepository.findByEmail(email)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
	}
}
