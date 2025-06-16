package com.study.petory.domain.user.repository;

import java.time.LocalDateTime;
import java.util.List;
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

	// 삭제일 85일~90일 사이인 사용자 (삭제 예정자)
	List<User> findByDeletedAtBetween(LocalDateTime from, LocalDateTime to);

	// 삭제일 90일 초과된 사용자 (하드 삭제 대상)
	List<User> findByDeletedAtBefore(LocalDateTime cutoff);
}
