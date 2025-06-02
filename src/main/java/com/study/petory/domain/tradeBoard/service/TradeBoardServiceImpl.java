package com.study.petory.domain.tradeBoard.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.study.petory.domain.tradeBoard.dto.request.TradeBoardCreateRequestDto;
import com.study.petory.domain.tradeBoard.dto.request.TradeBoardUpdateRequestDto;
import com.study.petory.domain.tradeBoard.dto.response.TradeBoardCreateResponseDto;
import com.study.petory.domain.tradeBoard.dto.response.TradeBoardGetResponseDto;
import com.study.petory.domain.tradeBoard.dto.response.TradeBoardUpdateResponseDto;
import com.study.petory.domain.tradeBoard.entity.TradeBoard;
import com.study.petory.domain.tradeBoard.entity.TradeCategory;
import com.study.petory.domain.tradeBoard.repository.TradeBoardRepository;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.repository.UserRepository;
import com.study.petory.exception.CustomException;
import com.study.petory.exception.enums.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TradeBoardServiceImpl implements TradeBoardService {

	private final TradeBoardRepository tradeBoardRepository;
	private final UserRepository userRepository;

	//게시글 생성
	@Override
	@Transactional
	public TradeBoardCreateResponseDto saveTradeBoard(TradeBoardCreateRequestDto requestDto) {

		//나중에 토큰으로 값을 받아올 예정
		User user = userRepository.findById(1L)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		TradeBoard tradeBoard = TradeBoard.builder()
			.category(requestDto.getCategory())
			.title(requestDto.getTitle())
			.content(requestDto.getContent())
			.photoUrl(requestDto.getPhotoUrl())
			.price(requestDto.getPrice())
			.user(user)
			.build();

		tradeBoardRepository.save(tradeBoard);

		return new TradeBoardCreateResponseDto(tradeBoard);
	}

	//게시글 전체 조회
	@Override
	@Transactional(readOnly = true)
	public Page<TradeBoardGetResponseDto> findAllTradeBoard(TradeCategory category, int page) {

		int adjustedPage = (page > 0) ? page - 1 : 0;
		PageRequest pageable = PageRequest.of(adjustedPage, 10, Sort.by("createdAt").descending());

		Page<TradeBoard> tradeBoard;
		if (category != null) { //카테고리가 있다면 카테고리로 조회
			tradeBoard = tradeBoardRepository.findAllByCategory(category, pageable);
		} else {
			tradeBoard = tradeBoardRepository.findAll(pageable);
		}

		return tradeBoard.map(TradeBoardGetResponseDto::new);
	}

	//게시글 단건 조회
	@Override
	@Transactional(readOnly = true)
	public TradeBoardGetResponseDto findByTradeBoardId(Long tradeBoardId) {

		TradeBoard tradeBoard = tradeBoardRepository.findTradeBoardById(tradeBoardId);

		return new TradeBoardGetResponseDto(tradeBoard);
	}

	//게시글 수정
	@Override
	@Transactional
	public TradeBoardUpdateResponseDto updateTradeBoard(Long tradeBoardId, TradeBoardUpdateRequestDto requestDto) {

		//나중에 토큰으로 값 받아오기
		User user = userRepository.findById(1L)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		TradeBoard tradeBoard = tradeBoardRepository.findTradeBoardById(tradeBoardId);

		if (tradeBoard.getUser() != user) {
			throw new CustomException(ErrorCode.TRADE_BOARD_FORBIDDEN);
		}

		if (requestDto.getCategory() != null) {
			tradeBoard.updateCategory(requestDto.getCategory());
		}

		if (requestDto.getTitle() != null && !requestDto.getTitle().isBlank()) {
			tradeBoard.updateTitle(requestDto.getTitle());
		}

		if (requestDto.getContent() != null && !requestDto.getContent().isBlank()) {
			tradeBoard.updateContent(requestDto.getContent());
		}

		if (requestDto.getPhotoUrl() != null && !requestDto.getPhotoUrl().isBlank()) {
			tradeBoard.updatePhotoUrl(requestDto.getPhotoUrl());
		}

		if (requestDto.getPrice() != null) {
			tradeBoard.updatePrice(requestDto.getPrice());
		}

		return new TradeBoardUpdateResponseDto(tradeBoard);
	}

	//User Status에 따라 상태값 변경하는 로직 구현 예정

	@Override
	@Transactional
	public void deleteTradeBoard(Long tradeBoardId) {

		//나중에 토큰으로 값 받아오기
		User user = userRepository.findById(1L)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		TradeBoard tradeBoard = tradeBoardRepository.findTradeBoardById(tradeBoardId);

		if (tradeBoard.getUser() != user) {
			throw new CustomException(ErrorCode.TRADE_BOARD_FORBIDDEN);
		}

		tradeBoard.deactivateEntity();
	}
}