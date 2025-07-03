package com.study.petory.domain.tradeboard.dto.response;

import java.time.LocalDateTime;

import com.study.petory.domain.tradeboard.entity.TradeBoard;
import com.study.petory.domain.tradeboard.entity.TradeBoardStatus;
import com.study.petory.domain.tradeboard.entity.TradeCategory;

import lombok.Getter;

@Getter
public class TradeBoardUpdateResponseDto {

	private final Long id;

	private final TradeCategory category;

	private final String title;

	private final String content;

	private final Integer price;

	private final TradeBoardStatus status;

	private final LocalDateTime createdAt;

	private final LocalDateTime updatedAt;

	public TradeBoardUpdateResponseDto(TradeBoard tradeBoard) {
		this.id = tradeBoard.getId();
		this.category = tradeBoard.getCategory();
		this.title = tradeBoard.getTitle();
		this.content = tradeBoard.getContent();
		this.price = tradeBoard.getPrice();
		this.status = tradeBoard.getStatus();
		this.createdAt = tradeBoard.getCreatedAt();
		this.updatedAt = tradeBoard.getUpdatedAt();
	}
}
