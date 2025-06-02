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
public class TradeBoardCreateRequestDto {

	@NotBlank(message = "카테고리를 입력해주세요.")
	private final TradeCategory category;

	@NotBlank(message = "제목을 입력해주세요.")
	@Size(max = 30, message = "제목의 길이가 너무 깁니다.")
	private final String title;

	@NotBlank
	@Size(max = 1000, message = "본문은 1000자를 넘어갈 수 없습니다.")
	private final String content;

	@Nullable
	private final String photoUrl; //변수의 타입은 S3 등록 후 변경 예정

	@NotNull(message = "금액을 입력해주세요.")
	private final Integer price;
}
