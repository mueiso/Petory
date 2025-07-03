package com.study.petory.domain.ownerboard.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.study.petory.domain.ownerboard.dto.response.OwnerBoardGetAllResponseDto;
import com.study.petory.domain.ownerboard.entity.OwnerBoard;

public interface OwnerBoardQueryRepository {

	Page<OwnerBoardGetAllResponseDto> findAllWithFirstImageAndTitleOptional(String title, Pageable pageable);

	Optional<OwnerBoard> findByIdWithImages(Long boardId);

	Optional<OwnerBoard> findByIdIncludingDeleted(Long boardId);

}
