package com.study.petory.domain.user.repository;

import java.util.Optional;

import com.study.petory.domain.user.entity.User;

public interface UserCustomRepository {

	Optional<User> findByEmailWithUserRole(String email);

	// userRole, userPrivateInfo 모두 필요 없는 경우
	Optional<User> findByIdWithUserRole(Long id);
}
