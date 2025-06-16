package com.study.petory.domain.ownerBoard.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.study.petory.domain.ownerBoard.entity.OwnerBoard;

public interface OwnerBoardQueryRepository {
	Page<OwnerBoard> findByTitleContaining(String title, Pageable pageable);

	Page<OwnerBoard> findAllWithImages(Pageable pageable);

	Optional<OwnerBoard> findByIdWithImages(Long boardId);

	Optional<OwnerBoard> findByIdIncludingDeleted(Long boardId);

}
