package com.study.petory.domain.tradeBoard.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.study.petory.domain.tradeBoard.dto.TradeBoardCreateRequestDto;
import com.study.petory.domain.tradeBoard.dto.TradeBoardCreateResponseDto;
import com.study.petory.domain.tradeBoard.entity.TradeBoard;
import com.study.petory.domain.tradeBoard.repository.TradeBoardRepository;
import com.study.petory.domain.user.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TradeBoardServiceImpl implements TradeBoardService{

	private final TradeBoardRepository tradeBoardRepository;

	@Override
	@Transactional
	public TradeBoardCreateResponseDto saveTradeBoard(TradeBoardCreateRequestDto requestDto) {

		User user = new User(); //나중에 토큰으로 값을 받아올 예정

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
}
