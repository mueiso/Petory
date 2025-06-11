package com.study.petory.domain.place.dto.request;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PlaceReviewCreateRequestDto {

	private final String content;

	private final BigDecimal ratio;
}
