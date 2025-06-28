package com.study.petory.domain.ownerBoard.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.study.petory.domain.ownerBoard.entity.OwnerBoard;
import com.study.petory.domain.user.entity.User;

public interface OwnerBoardRepository extends JpaRepository<OwnerBoard, Long>, OwnerBoardQueryRepository {

	List<OwnerBoard> findByUser(User user);
}
