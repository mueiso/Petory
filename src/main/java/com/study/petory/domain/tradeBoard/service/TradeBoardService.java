package com.study.petory.domain.tradeBoard.service;

import com.study.petory.domain.tradeBoard.dto.TradeBoardCreateRequestDto;
import com.study.petory.domain.tradeBoard.dto.TradeBoardCreateResponseDto;

public interface TradeBoardService {

	TradeBoardCreateResponseDto saveTradeBoard(TradeBoardCreateRequestDto requestDto);
}
