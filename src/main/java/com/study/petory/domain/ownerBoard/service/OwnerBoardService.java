package com.study.petory.domain.ownerBoard.service;

import com.study.petory.domain.ownerBoard.dto.request.OwnerBoardCreateRequestDto;
import com.study.petory.domain.ownerBoard.dto.response.OwnerBoardCreateResponseDto;
import com.study.petory.domain.ownerBoard.dto.response.OwnerBoardGetResponseDto;
import com.study.petory.domain.ownerBoard.entity.OwnerBoard;

import jakarta.validation.Valid;

public interface OwnerBoardService {
	OwnerBoard findOwnerBoardById(Long boardId);

	OwnerBoardCreateResponseDto saveOwnerBoard(@Valid OwnerBoardCreateRequestDto dto);

	OwnerBoardGetResponseDto findOwnerBoard(Long boardId);
}
