package com.study.petory.domain.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.study.petory.common.exception.CustomException;
import com.study.petory.common.exception.enums.ErrorCode;
import com.study.petory.domain.user.dto.UpdateUserRequestDto;
import com.study.petory.domain.user.dto.UserProfileResponseDto;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	// 현재 사용자 정보 조회
	@Override
	@Transactional(readOnly = true)
	public UserProfileResponseDto getMyProfile(String email) {

		User user = userRepository.findByEmail(email)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		return new UserProfileResponseDto(user.getEmail(), user.getNickname());
	}

	// 사용자 정보 업데이트
	@Override
	@Transactional
	public void updateProfile(String email, UpdateUserRequestDto dto) {

		User user = userRepository.findByEmail(email)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		user.updateNickname(dto.getNickname());
	}

	// 사용자 탈퇴
	@Override
	@Transactional
	public void deleteAccount(String email) {

		User user = userRepository.findByEmail(email)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		userRepository.delete(user);
		// 또는 user.markAsDeleted(); -> 소프트 삭제를 원한다면
	}
}
