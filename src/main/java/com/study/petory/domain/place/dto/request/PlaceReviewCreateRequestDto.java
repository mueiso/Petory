package com.study.petory.domain.place.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PlaceReviewCreateRequestDto {

	@NotBlank
	@Size(max = 255, message = "리뷰 내용은 255자까지만 입력 가능합니다.")
	private final String content;

	@Digits(integer = 1, fraction = 0) // 정수 한자리만 가능
	@DecimalMin(value = "1", message = "평점은 1점 이상이어야 합니다.")
	@DecimalMax(value = "5", message = "평점은 5점 이하여야 합니다.")
	private final BigDecimal ratio;
}
