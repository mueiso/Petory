package com.study.petory.domain.user.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.entity.UserStatus;

public interface UserRepository extends JpaRepository<User, Long> {

	@EntityGraph(attributePaths = {"userRole"})
	Optional<User> findByEmail(String email);

	// userRole, userPrivateInfo 모두 필요 없는 경우
	@EntityGraph(attributePaths = {"userRole"})
	Optional<User> findUserById(Long id);

	// UserStatus 가 DEACTIVATED + deletedAt 85일~90일 사이인 사용자 (hard delete 예정자 조회용)
	List<User> findByUserStatusAndDeletedAtBetween(UserStatus userStatus, LocalDateTime from, LocalDateTime to);

	// UserStatus 가 DEACTIVATED/DELETED + deletedAt 90일 초과된 사용자 (hard delete 대상 조회용)
	List<User> findByUserStatusInAndDeletedAtBefore(List<UserStatus> statusList, LocalDateTime deletedAtBefore);

	// UserStatus 가 DEACTIVATED +  deletedAt 특정 시간 이전인 사용자 조회 (휴면 후 특정 기간 지난 사용자 조회용)
	List<User> findByUserStatusAndDeletedAtBefore(UserStatus userStatus, LocalDateTime time);

	// UserStatus 가 ACTIVE 상태이고, updatedAt 이 특정 시간 이전인 사용자 조회 (마지막 활동 시점이 오래된 사용자 조회용)
	List<User> findByUserStatusAndUpdatedAtBefore(UserStatus userStatus, LocalDateTime updatedAtBefore);

	@EntityGraph(attributePaths = "userPrivateInfo")
	List<User> findAll();
}
