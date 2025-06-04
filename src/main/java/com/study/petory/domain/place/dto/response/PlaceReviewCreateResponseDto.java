package com.study.petory.domain.place.dto.response;

import java.time.LocalDateTime;

import com.study.petory.domain.place.entity.PlaceReview;

import lombok.Getter;

@Getter
public class PlaceReviewCreateResponseDto {

	private final Long placeId;

	private final String nickName;

	private final Long id;

	private final String content;

	private final Integer ratio;

	private final LocalDateTime createdAt;

	private PlaceReviewCreateResponseDto(PlaceReview placeReview) {
		this.placeId = placeReview.getPlace().getId();
		this.nickName = placeReview.getUser().getNickname();
		this.id = placeReview.getId();
		this.content = placeReview.getContent();
		this.ratio = placeReview.getRatio();
		this.createdAt = placeReview.getCreatedAt();
	}

	public static PlaceReviewCreateResponseDto from(PlaceReview placeReview) {
		return new PlaceReviewCreateResponseDto(placeReview);
	}
}
