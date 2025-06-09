package com.study.petory.domain.tradeBoard.dto.response;

import com.study.petory.domain.tradeBoard.entity.TradeBoard;

import lombok.Getter;

@Getter
public class TradeBoardGetAllResponseDto {

	private final Long tradeBoardId;

	private final String nickName;

	private final String title;

	private final String content;

	private final String photoUrl;

	private final Integer price;

	public TradeBoardGetAllResponseDto(TradeBoard tradeBoard) {
		this.tradeBoardId = tradeBoard.getId();
		this.nickName = tradeBoard.getUser().getNickname();
		this.title = tradeBoard.getTitle();
		this.content = (tradeBoard.getContent().length() > 30) ?
			tradeBoard.getContent().substring(0, 30) + "..." : tradeBoard.getContent();
		this.photoUrl = tradeBoard.getPhotoUrl();
		this.price = tradeBoard.getPrice();
	}
}
