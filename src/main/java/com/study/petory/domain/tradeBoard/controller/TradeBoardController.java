package com.study.petory.domain.tradeBoard.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.study.petory.common.response.CommonResponse;
import com.study.petory.domain.tradeBoard.dto.TradeBoardCreateRequestDto;
import com.study.petory.domain.tradeBoard.dto.TradeBoardCreateResponseDto;
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
}
