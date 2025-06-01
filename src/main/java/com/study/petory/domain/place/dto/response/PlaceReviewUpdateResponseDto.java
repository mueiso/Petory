package com.study.petory.domain.place.dto.response;

import java.time.LocalDateTime;

import com.study.petory.domain.place.entity.PlaceReview;

import lombok.Getter;

@Getter
public class PlaceReviewUpdateResponseDto {

	private final Long placeId;

	private final Long userId;

	private final Long id;

	private final String content;

	private final Integer ratio;

	private final LocalDateTime updatedAt;

	private PlaceReviewUpdateResponseDto(PlaceReview placeReview) {
		this.placeId = placeReview.getPlace().getId();
		this.userId = placeReview.getUser().getId();
		this.id = placeReview.getId();
		this.content = placeReview.getContent();
		this.ratio = placeReview.getRatio();
		this.updatedAt = placeReview.getUpdatedAt();
	}

	public static PlaceReviewUpdateResponseDto from(PlaceReview placeReview) {
		return new PlaceReviewUpdateResponseDto(placeReview);
	}
}
