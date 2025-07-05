package com.study.petory.domain.user.repository;

import java.util.Optional;

import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.entity.UserStatus;

public interface UserQueryRepository {

	Optional<User> findByEmailWithUserRole(String email);

	Optional<User> findByIdWithUserRole(Long id);

	Optional<User> findByIdWithUserRoleAndUserStatus(Long id, UserStatus status);
}
