package com.study.petory.domain.ownerBoard.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.study.petory.domain.ownerBoard.entity.OwnerBoard;

public interface OwnerBoardRepository extends JpaRepository<OwnerBoard, Long>, OwnerBoardQueryRepository {

}
