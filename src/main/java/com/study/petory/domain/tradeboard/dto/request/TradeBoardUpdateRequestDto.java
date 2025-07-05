package com.study.petory.domain.tradeboard.dto.request;

import com.study.petory.domain.tradeboard.entity.TradeBoardStatus;
import com.study.petory.domain.tradeboard.entity.TradeCategory;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TradeBoardUpdateRequestDto {

	@NotNull(message = "카테고리를 입력해주세요.")
	private final TradeCategory category;

	@NotBlank(message = "제목을 입력해주세요.")
	@Size(max = 30, message = "제목의 길이가 너무 깁니다.")
	private final String title;

	@NotBlank
	@Size(max = 1000, message = "본문은 1000자를 넘어갈 수 없습니다.")
	private final String content;

	@NotNull(message = "금액을 입력해주세요.")
	private final Integer price;

	@NotNull(message = "판매하시는 물품의 상태를 입력해주세요.")
	private final TradeBoardStatus status;
}