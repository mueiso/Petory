package com.study.petory.domain.tradeboard.dto.response;

import java.util.List;

import com.study.petory.domain.tradeboard.entity.TradeBoard;
import com.study.petory.domain.tradeboard.entity.TradeBoardImage;

import lombok.Getter;

@Getter
public class TradeBoardGetAllResponseDto {

	private final Long tradeBoardId;

	private final String nickName;

	private final String title;

	private final String content;

	private final Integer price;

	private final List<TradeBoardImage> images;

	public TradeBoardGetAllResponseDto(TradeBoard tradeBoard) {
		this.tradeBoardId = tradeBoard.getId();
		this.nickName = tradeBoard.getUser().getNickname();
		this.title = tradeBoard.getTitle();
		this.content = (tradeBoard.getContent().length() > 30) ?
			tradeBoard.getContent().substring(0, 30) + "..." : tradeBoard.getContent();
		this.images = tradeBoard.getImages();
		this.price = tradeBoard.getPrice();
	}
}
