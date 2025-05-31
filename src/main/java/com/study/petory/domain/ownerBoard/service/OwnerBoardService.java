package com.study.petory.domain.ownerBoard.service;

import com.study.petory.domain.ownerBoard.dto.request.OwnerBoardCreateRequestDto;
import com.study.petory.domain.ownerBoard.dto.response.OwnerBoardCreateResponseDto;

import jakarta.validation.Valid;

public interface OwnerBoardService {
	OwnerBoardCreateResponseDto saveOwnerBoard(@Valid OwnerBoardCreateRequestDto dto);
}
