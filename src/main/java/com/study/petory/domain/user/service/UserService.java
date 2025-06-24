package com.study.petory.domain.user.service;

import com.study.petory.domain.user.dto.TokenResponseDto;
import com.study.petory.domain.user.dto.UpdateUserRequestDto;
import com.study.petory.domain.user.dto.UserProfileResponseDto;
import com.study.petory.domain.user.entity.User;

public interface UserService {

	TokenResponseDto testLogin(Long userId);

	UserProfileResponseDto findMyProfile(String email);

	void updateProfile(String email, UpdateUserRequestDto dto);

	void deleteAccount(String email);

	User findUserById(Long userId);

	User findUserByEmail(String email);
}
