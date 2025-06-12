package com.study.petory.domain.user.service;

import com.study.petory.domain.user.dto.UpdateUserRequestDto;
import com.study.petory.domain.user.dto.UserProfileResponseDto;

public interface UserService {

	UserProfileResponseDto getMyProfile(String email);

	void updateProfile(String email, UpdateUserRequestDto dto);

	void deleteAccount(String email);
}
