package com.study.petory.domain.tradeBoard.dto.request;

import com.study.petory.domain.tradeBoard.entity.TradeCategory;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TradeBoardUpdateRequestDto {

	private final TradeCategory category;

	@Size(max = 30, message = "제목의 길이가 너무 깁니다.")
	private final String title;

	@Size(max = 1000, message = "본문은 1000자를 넘어갈 수 없습니다.")
	private final String content;

	private final String photoUrl;

	private final Integer price;
}