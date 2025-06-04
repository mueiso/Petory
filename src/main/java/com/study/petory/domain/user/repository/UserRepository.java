package com.study.petory.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.study.petory.domain.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	User findByEmail(String email)
}
