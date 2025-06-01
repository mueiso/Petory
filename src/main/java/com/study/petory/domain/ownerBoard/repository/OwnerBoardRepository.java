package com.study.petory.domain.ownerBoard.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import com.study.petory.domain.ownerBoard.entity.OwnerBoard;

public interface OwnerBoardRepository extends JpaRepository<OwnerBoard, Long> {

	Page<OwnerBoard> findByTitleContaining(String title, PageRequest pageRequest);
}
