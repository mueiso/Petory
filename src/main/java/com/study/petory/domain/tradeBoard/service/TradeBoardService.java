package com.study.petory.domain.tradeBoard.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.study.petory.domain.tradeBoard.dto.request.TradeBoardCreateRequestDto;
import com.study.petory.domain.tradeBoard.dto.request.TradeBoardUpdateRequestDto;
import com.study.petory.domain.tradeBoard.dto.response.TradeBoardCreateResponseDto;
import com.study.petory.domain.tradeBoard.dto.response.TradeBoardGetAllResponseDto;
import com.study.petory.domain.tradeBoard.dto.response.TradeBoardGetResponseDto;
import com.study.petory.domain.tradeBoard.dto.response.TradeBoardUpdateResponseDto;
import com.study.petory.domain.tradeBoard.entity.TradeBoard;
import com.study.petory.domain.tradeBoard.entity.TradeBoardStatus;
import com.study.petory.domain.tradeBoard.entity.TradeCategory;

public interface TradeBoardService {

	TradeBoardCreateResponseDto saveTradeBoard(TradeBoardCreateRequestDto requestDto, List<MultipartFile> images);

	Page<TradeBoardGetAllResponseDto> findAllTradeBoard(TradeCategory category, Pageable pageable);

	TradeBoardGetResponseDto findByTradeBoardId(Long tradeBoardId);

	TradeBoardUpdateResponseDto updateTradeBoard(Long tradeBoardId, TradeBoardUpdateRequestDto requestDto);

	void updateTradeBoardStatus(Long tradeBoardId, TradeBoardStatus status);

	TradeBoard findTradeBoardById(Long tradeBoardId);

	void deleteImage(Long tradeBoardId, Long imageId);
}
