package com.study.petory.domain.tradeBoard.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.study.petory.domain.tradeBoard.entity.TradeBoard;
import com.study.petory.domain.tradeBoard.entity.TradeBoardImage;
import com.study.petory.domain.tradeBoard.entity.TradeCategory;

import lombok.Getter;

@Getter
public class TradeBoardGetResponseDto {

	private final Long id;

	private final TradeCategory category;

	private final String title;

	private final String content;

	private final List<TradeBoardImage> images;

	private final Integer price;

	private final LocalDateTime createdAt;

	private final LocalDateTime updatedAt;

	public TradeBoardGetResponseDto(TradeBoard tradeBoard) {
		this.id = tradeBoard.getId();
		this.category = tradeBoard.getCategory();
		this.title = tradeBoard.getTitle();
		this.content = tradeBoard.getContent();
		this.images = tradeBoard.getImages();
		this.price = tradeBoard.getPrice();
		this.createdAt = tradeBoard.getCreatedAt();
		this.updatedAt = tradeBoard.getUpdatedAt();
	}

}
