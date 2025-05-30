package com.study.petory.domain.tradeBoard.service;

import org.springframework.data.domain.Page;

import com.study.petory.domain.tradeBoard.dto.request.TradeBoardCreateRequestDto;
import com.study.petory.domain.tradeBoard.dto.request.TradeBoardUpdateRequestDto;
import com.study.petory.domain.tradeBoard.dto.response.TradeBoardCreateResponseDto;
import com.study.petory.domain.tradeBoard.dto.response.TradeBoardGetResponseDto;
import com.study.petory.domain.tradeBoard.dto.response.TradeBoardUpdateResponseDto;
import com.study.petory.domain.tradeBoard.entity.TradeCategory;

public interface TradeBoardService {

	TradeBoardCreateResponseDto saveTradeBoard(TradeBoardCreateRequestDto requestDto);

	Page<TradeBoardGetResponseDto> findAllTradeBoard(TradeCategory category, int page);

	TradeBoardGetResponseDto findByTradeBoardId(Long tradeBoardId);

	TradeBoardUpdateResponseDto updateTradeBoard(Long tradeBoardId, TradeBoardUpdateRequestDto requestDto);

	void deleteTradeBoard(Long tradeBoardId);
}
