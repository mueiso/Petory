package com.study.petory.domain.tradeboard.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.study.petory.domain.tradeboard.dto.request.TradeBoardCreateRequestDto;
import com.study.petory.domain.tradeboard.dto.request.TradeBoardUpdateRequestDto;
import com.study.petory.domain.tradeboard.dto.response.TradeBoardCreateResponseDto;
import com.study.petory.domain.tradeboard.dto.response.TradeBoardGetAllResponseDto;
import com.study.petory.domain.tradeboard.dto.response.TradeBoardGetResponseDto;
import com.study.petory.domain.tradeboard.dto.response.TradeBoardUpdateResponseDto;
import com.study.petory.domain.tradeboard.entity.TradeBoardStatus;
import com.study.petory.domain.tradeboard.entity.TradeCategory;

public interface TradeBoardService {

	TradeBoardCreateResponseDto saveTradeBoard(Long userId, TradeBoardCreateRequestDto requestDto,
		List<MultipartFile> images);

	Page<TradeBoardGetAllResponseDto> findAllTradeBoard(TradeCategory category, Pageable pageable);

	TradeBoardGetResponseDto findByTradeBoardId(Long tradeBoardId);

	Page<TradeBoardGetAllResponseDto> findByUser(Long userId, Pageable pageable);

	TradeBoardUpdateResponseDto updateTradeBoard(Long userId, Long tradeBoardId, TradeBoardUpdateRequestDto requestDto);

	void updateTradeBoardStatus(Long userId, Long tradeBoardId, TradeBoardStatus status);

	void addImages(Long userId, Long tradeBoardId, List<MultipartFile> images);

	void deleteImage(Long userId, Long tradeBoardId, Long imageId);

}
