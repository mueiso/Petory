package com.study.petory.domain.tradeBoard.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.study.petory.domain.tradeBoard.dto.request.TradeBoardCreateRequestDto;
import com.study.petory.domain.tradeBoard.dto.request.TradeBoardUpdateRequestDto;
import com.study.petory.domain.tradeBoard.dto.response.TradeBoardCreateResponseDto;
import com.study.petory.domain.tradeBoard.dto.response.TradeBoardGetAllResponseDto;
import com.study.petory.domain.tradeBoard.dto.response.TradeBoardGetResponseDto;
import com.study.petory.domain.tradeBoard.dto.response.TradeBoardUpdateResponseDto;
import com.study.petory.domain.tradeBoard.entity.TradeBoard;
import com.study.petory.domain.tradeBoard.entity.TradeBoardStatus;
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

	//tradeBoardId로 tradeBoard 조회
	@Override
	public TradeBoard findTradeBoardById(Long tradeBoardId) {
		return tradeBoardRepository.findById(tradeBoardId)
			.orElseThrow(() -> new CustomException(ErrorCode.TRADE_BOARD_NOT_FOUND));
	}

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
	public Page<TradeBoardGetAllResponseDto> findAllTradeBoard(TradeCategory category, Pageable pageable) {

		Page<TradeBoard> tradeBoard;
		if (category != null) { //카테고리가 있다면 카테고리로 조회
			tradeBoard = tradeBoardRepository.findAllByCategory(category, pageable);
		} else {
			tradeBoard = tradeBoardRepository.findAll(pageable);
		}

		return tradeBoard.map(TradeBoardGetAllResponseDto::new);
	}

	//게시글 단건 조회
	@Override
	@Transactional(readOnly = true)
	public TradeBoardGetResponseDto findByTradeBoardId(Long tradeBoardId) {

		TradeBoard tradeBoard = findTradeBoardById(tradeBoardId);

		return new TradeBoardGetResponseDto(tradeBoard);
	}

	//게시글 수정
	@Override
	@Transactional
	public TradeBoardUpdateResponseDto updateTradeBoard(Long tradeBoardId, TradeBoardUpdateRequestDto requestDto) {

		//나중에 토큰으로 값 받아오기
		User loginUser = userRepository.findById(1L)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		TradeBoard tradeBoard = findTradeBoardById(tradeBoardId);

		//로그인 유저와 게시글의 유저가 다를 경우 예외처리
		if (!tradeBoard.isEqualUser(loginUser.getId())) {
			throw new CustomException(ErrorCode.FORBIDDEN);
		}

		tradeBoard.updateTradeBoard(requestDto);

		return new TradeBoardUpdateResponseDto(tradeBoard);
	}

	//게시글 상태 업데이트
	@Override
	@Transactional
	public void updateTradeBoardStatus(Long tradeBoardId, TradeBoardStatus status) {

		//토큰 값으로 변경 예정
		User loginUser = userRepository.findById(1L)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		TradeBoard tradeBoard = findTradeBoardById(tradeBoardId);

		//로그인 유저와 게시글의 유저가 다를 경우 예외처리
		if (!tradeBoard.isEqualUser(loginUser.getId())) {
			throw new CustomException(ErrorCode.FORBIDDEN);
		}

		tradeBoard.updateStatus(status);
	}

}