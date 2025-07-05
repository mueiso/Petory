package com.study.petory.domain.ownerboard.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.study.petory.domain.ownerboard.entity.OwnerBoard;
import com.study.petory.domain.user.entity.User;

public interface OwnerBoardRepository extends JpaRepository<OwnerBoard, Long>, OwnerBoardQueryRepository {

	List<OwnerBoard> findByUser(User user);
}
