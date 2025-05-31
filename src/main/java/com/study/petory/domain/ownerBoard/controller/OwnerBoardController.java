package com.study.petory.domain.ownerBoard.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.study.petory.common.response.CommonResponse;
import com.study.petory.domain.ownerBoard.dto.request.OwnerBoardCreateRequestDto;
import com.study.petory.domain.ownerBoard.dto.response.OwnerBoardCreateResponseDto;
import com.study.petory.domain.ownerBoard.service.OwnerBoardService;
import com.study.petory.exception.enums.SuccessCode;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/owner-boards")
public class OwnerBoardController {

	private final OwnerBoardService ownerBoardService;

	// 게시글 생성
	CommonResponse<OwnerBoardCreateResponseDto> createOwnerBoard(@Valid OwnerBoardCreateRequestDto dto) {
		OwnerBoardCreateResponseDto result = ownerBoardService.saveOwnerBoard(dto);

		return CommonResponse.of(SuccessCode.CREATED, result); //추가하기
	}
}
