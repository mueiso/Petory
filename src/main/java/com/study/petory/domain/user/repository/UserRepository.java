package com.study.petory.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.study.petory.domain.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

	@EntityGraph(attributePaths = {"userRole"})
	Optional<User> findByEmail(String email);

	// userRole, userPrivateInfo 모두 필요 없는 경우
	@EntityGraph(attributePaths = {"userRole"})
	Optional<User> findUserById(Long id);
}
