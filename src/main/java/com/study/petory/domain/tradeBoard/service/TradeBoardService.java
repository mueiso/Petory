package com.study.petory.domain.tradeBoard.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.study.petory.common.security.CustomPrincipal;
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

	TradeBoardCreateResponseDto saveTradeBoard(Long userId, TradeBoardCreateRequestDto requestDto, List<MultipartFile> images);

	Page<TradeBoardGetAllResponseDto> findAllTradeBoard(TradeCategory category, Pageable pageable);

	TradeBoardGetResponseDto findByTradeBoardId(Long tradeBoardId);

	Page<TradeBoardGetAllResponseDto> findByUser(Long userId, Pageable pageable);

	TradeBoardUpdateResponseDto updateTradeBoard(Long userId, Long tradeBoardId, TradeBoardUpdateRequestDto requestDto);

	void updateTradeBoardStatus(Long userId, Long tradeBoardId, TradeBoardStatus status);

	void addImages(Long userId, Long tradeBoardId, List<MultipartFile> images);

	void deleteImage(Long userId, Long tradeBoardId, Long imageId);

}
