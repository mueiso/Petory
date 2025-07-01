package com.study.petory.domain.user.repository;

import java.util.Optional;

import com.study.petory.domain.user.entity.User;

public interface UserCustomRepository {

	Optional<User> findByEmailWithUserRole(String email);

	Optional<User> findByIdWithUserRole(Long id);
}
