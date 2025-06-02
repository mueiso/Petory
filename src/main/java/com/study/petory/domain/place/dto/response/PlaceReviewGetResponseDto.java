package com.study.petory.domain.place.dto.response;

import com.study.petory.domain.place.entity.PlaceReview;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class PlaceReviewGetResponseDto {

	private final Long id;

	private final String content;

	private final Integer ratio;

	private PlaceReviewGetResponseDto(PlaceReview placeReview) {
		this.id = placeReview.getId();
		this.content = placeReview.getContent();
		this.ratio = placeReview.getRatio();
	}

	public static PlaceReviewGetResponseDto from(PlaceReview placeReview) {
		return new PlaceReviewGetResponseDto(placeReview);
	}
}
