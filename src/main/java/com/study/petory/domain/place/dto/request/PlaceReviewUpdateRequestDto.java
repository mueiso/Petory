package com.study.petory.domain.place.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PlaceReviewUpdateRequestDto {

	private final String content;

	private final Integer ratio;
}
