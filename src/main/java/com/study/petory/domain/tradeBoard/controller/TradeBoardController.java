package com.study.petory.domain.tradeBoard.controller;

import org.springframework.data.domain.Page;
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
import com.study.petory.domain.tradeBoard.dto.response.TradeBoardGetResponseDto;
import com.study.petory.domain.tradeBoard.dto.response.TradeBoardUpdateResponseDto;
import com.study.petory.domain.tradeBoard.entity.TradeCategory;
import com.study.petory.domain.tradeBoard.service.TradeBoardServiceImpl;
import com.study.petory.exception.enums.SuccessCode;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/trade-boards")
@RequiredArgsConstructor
public class TradeBoardController {

	private final TradeBoardServiceImpl tradeBoardService;

	@PostMapping
	private CommonResponse<TradeBoardCreateResponseDto> createTradeBoard(@RequestBody TradeBoardCreateRequestDto requestDto) {

		return CommonResponse.of(SuccessCode.CREATED, tradeBoardService.saveTradeBoard(requestDto));
	}

	@GetMapping
	private CommonResponse<Page<TradeBoardGetResponseDto>> getAllTradeBoard(
		@RequestParam(required = false) TradeCategory category,
		@RequestParam(defaultValue = "1") int page
	) {
		return CommonResponse.of(SuccessCode.OK, tradeBoardService.findAllTradeBoard(category, page));
	}

	@GetMapping("/{tradeBoardId}")
	private CommonResponse<TradeBoardGetResponseDto> getByTradeBoardId(
		@PathVariable Long tradeBoardId,
		@RequestParam(defaultValue = "1") int page
	) {
		return CommonResponse.of(SuccessCode.OK, tradeBoardService.findByTradeBoardId(tradeBoardId));
	}

	@PatchMapping("/{tradeBoardId}")
	private CommonResponse<TradeBoardUpdateResponseDto> updateTradeBoard(
		@PathVariable Long tradeBoardId,
		@RequestBody TradeBoardUpdateRequestDto requestDto
	) {
		return CommonResponse.of(SuccessCode.OK, tradeBoardService.updateTradeBoard(tradeBoardId, requestDto));
	}

	@DeleteMapping("/{tradeBoardId}")
	private CommonResponse<Void> deleteTradeBoard(@PathVariable Long tradeBoardId) {
		tradeBoardService.deleteTradeBoard(tradeBoardId);
		return CommonResponse.of(SuccessCode.NO_CONTENT);
	}
}
