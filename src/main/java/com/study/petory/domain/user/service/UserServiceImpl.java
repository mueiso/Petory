package com.study.petory.domain.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.study.petory.common.exception.CustomException;
import com.study.petory.common.exception.enums.ErrorCode;
import com.study.petory.domain.user.dto.UpdateUserRequestDto;
import com.study.petory.domain.user.dto.UserProfileResponseDto;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.entity.UserPrivateInfo;
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

		// UserPrivateInfo 조회
		UserPrivateInfo userPrivateInfo = user.getUserPrivateInfo();

		return new UserProfileResponseDto(
			user.getEmail(),
			user.getNickname(),
			userPrivateInfo.getName(),
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
		user.getUserPrivateInfo().update(dto.getNickname(), dto.getMobileNum());
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

	// TODO - 사용자 계정 정지 및 비활성화 위한 API 필요
}
