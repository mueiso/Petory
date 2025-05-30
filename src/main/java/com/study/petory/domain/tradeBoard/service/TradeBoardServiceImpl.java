package com.study.petory.domain.tradeBoard.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.study.petory.domain.tradeBoard.dto.request.TradeBoardCreateRequestDto;
import com.study.petory.domain.tradeBoard.dto.response.TradeBoardCreateResponseDto;
import com.study.petory.domain.tradeBoard.dto.response.TradeBoardGetResponseDto;
import com.study.petory.domain.tradeBoard.entity.TradeBoard;
import com.study.petory.domain.tradeBoard.entity.TradeCategory;
import com.study.petory.domain.tradeBoard.repository.TradeBoardRepository;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TradeBoardServiceImpl implements TradeBoardService{

	private final TradeBoardRepository tradeBoardRepository;
	private final UserRepository userRepository;

	@Override
	@Transactional
	public TradeBoardCreateResponseDto saveTradeBoard(TradeBoardCreateRequestDto requestDto) {

		//User user = new User();//나중에 토큰으로 값을 받아올 예정
		User user = userRepository.findById(1L).orElseThrow();

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

	@Override
	public Page<TradeBoardGetResponseDto> findAllTradeBoard(TradeCategory category, int page) {

		int adjustedPage = (page > 0) ? page - 1 : 0;
		PageRequest pageable = PageRequest.of(adjustedPage, 10, Sort.by("createdAt").descending());

		Page<TradeBoard> tradeBoard;
		if (category != null) {
			tradeBoard = tradeBoardRepository.findAllByCategory(category, pageable);
		} else {
			tradeBoard = tradeBoardRepository.findAll(pageable);
		}

		return tradeBoard.map(TradeBoardGetResponseDto::new);
	}
}
