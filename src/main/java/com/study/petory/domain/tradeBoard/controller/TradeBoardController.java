package com.study.petory.domain.tradeBoard.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.study.petory.common.response.CommonResponse;
import com.study.petory.domain.tradeBoard.dto.request.TradeBoardCreateRequestDto;
import com.study.petory.domain.tradeBoard.dto.request.TradeBoardUpdateRequestDto;
import com.study.petory.domain.tradeBoard.dto.response.TradeBoardCreateResponseDto;
import com.study.petory.domain.tradeBoard.dto.response.TradeBoardGetAllResponseDto;
import com.study.petory.domain.tradeBoard.dto.response.TradeBoardGetResponseDto;
import com.study.petory.domain.tradeBoard.dto.response.TradeBoardUpdateResponseDto;
import com.study.petory.domain.tradeBoard.entity.TradeBoardStatus;
import com.study.petory.domain.tradeBoard.entity.TradeCategory;
import com.study.petory.domain.tradeBoard.service.TradeBoardService;
import com.study.petory.exception.enums.SuccessCode;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/trade-boards")
@RequiredArgsConstructor
public class TradeBoardController {

	private final TradeBoardService tradeBoardService;

	/**
	 * 게시글 등록
	 * @param requestDto 카테고리, 제목, 내용, 사진, 금액
	 * @return id, 카테고리, 제목, 내용, 사진, 금액, 생성일 반환
	 */
	@PostMapping
	private ResponseEntity<CommonResponse<TradeBoardCreateResponseDto>> createTradeBoard(
		@Valid @RequestBody TradeBoardCreateRequestDto requestDto
	) {

		return CommonResponse.of(SuccessCode.CREATED, tradeBoardService.saveTradeBoard(requestDto));
	}

	/**
	 * 게시글 전체 조회
	 * @param category 카테고리 (nullable)
	 * @param page 조회하려는 페이지 위치
	 * @return List 형식의 게시글 반환
	 */
	@GetMapping
	private ResponseEntity<CommonResponse<Page<TradeBoardGetAllResponseDto>>> getAllTradeBoard(
		@RequestParam(required = false) TradeCategory category,
		@RequestParam(defaultValue = "1") int page
	) {
		return CommonResponse.of(SuccessCode.FOUND, tradeBoardService.findAllTradeBoard(category, page));
	}

	/**
	 * 게시글 단건 조회
	 * @param tradeBoardId 조회하려는 게시글 id
	 * @param page 조회하려는 페이지 위치
	 * @return 해당 게시글 반환
	 */
	@GetMapping("/{tradeBoardId}")
	private ResponseEntity<CommonResponse<TradeBoardGetResponseDto>> getByTradeBoardId(
		@PathVariable Long tradeBoardId,
		@RequestParam(defaultValue = "1") int page
	) {
		return CommonResponse.of(SuccessCode.FOUND, tradeBoardService.findByTradeBoardId(tradeBoardId));
	}

	/**
	 * 게시글 수정
	 * @param tradeBoardId 수정하려는 게시글 id
	 * @param requestDto 카테고리, 제목, 내용, 사진, 금액 (nullable)
	 * @return id, 카테고리, 제목, 내용, 사진, 금액, 생성일, 수정일 반환
	 */
	@PatchMapping("/{tradeBoardId}/1")
	private ResponseEntity<CommonResponse<TradeBoardUpdateResponseDto>> updateTradeBoard(
		@PathVariable Long tradeBoardId,
		@Valid @RequestBody TradeBoardUpdateRequestDto requestDto
	) {
		return CommonResponse.of(SuccessCode.UPDATED, tradeBoardService.updateTradeBoard(tradeBoardId, requestDto));
	}

	@PatchMapping("/{tradeBoardId}")
	private ResponseEntity<CommonResponse<Void>> updateTradeBoardStatus(
		@PathVariable Long tradeBoardId,
		@RequestParam TradeBoardStatus status
	) {
		tradeBoardService.updateTradeBoardStatus(tradeBoardId, status);
		return CommonResponse.of(SuccessCode.UPDATED);
	}

}
