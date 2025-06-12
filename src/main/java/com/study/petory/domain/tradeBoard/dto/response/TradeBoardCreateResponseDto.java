package com.study.petory.domain.tradeBoard.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.study.petory.domain.tradeBoard.entity.TradeBoard;
import com.study.petory.domain.tradeBoard.entity.TradeCategory;

import lombok.Getter;

@Getter
public class TradeBoardCreateResponseDto {

	private final Long id;

	private final TradeCategory category;

	private final String title;

	private final String content;

	private final List<String> imageUrls;

	private final Integer price;

	private final LocalDateTime createdAt;

	public TradeBoardCreateResponseDto(TradeBoard tradeBoard, List<String> imageUrls) {
		this.id = tradeBoard.getId();
		this.category = tradeBoard.getCategory();
		this.title = tradeBoard.getTitle();
		this.content = tradeBoard.getContent();
		this.imageUrls = imageUrls;
		this.price = tradeBoard.getPrice();
		this.createdAt = tradeBoard.getCreatedAt();
	}
}
