package com.study.petory.domain.place.dto.response;

import java.math.BigDecimal;

import com.study.petory.domain.place.entity.PlaceReview;

import lombok.Getter;

@Getter
public class PlaceReviewGetResponseDto {

	private final Long id;

	private final String nickName;

	private final String content;

	private final BigDecimal ratio;

	private PlaceReviewGetResponseDto(PlaceReview placeReview) {
		this.id = placeReview.getId();
		this.nickName = placeReview.getUser().getNickname();
		this.content = placeReview.getContent();
		this.ratio = placeReview.getRatio();
	}

	public static PlaceReviewGetResponseDto from(PlaceReview placeReview) {
		return new PlaceReviewGetResponseDto(placeReview);
	}
}
