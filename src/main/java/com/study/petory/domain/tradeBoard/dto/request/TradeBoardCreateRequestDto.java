package com.study.petory.domain.tradeBoard.dto.request;

import com.study.petory.domain.tradeBoard.entity.TradeCategory;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TradeBoardCreateRequestDto {

	private final TradeCategory category;

	private final String title;

	private final String content;

	private final String photoUrl;

	private final Integer price;
}
