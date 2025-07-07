package com.study.petory.domain.place.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.study.petory.domain.place.entity.PlaceReview;

import lombok.Getter;

@Getter
public class PlaceReviewUpdateResponseDto {

	private final Long placeId;

	private final String nickName;

	private final Long id;

	private final String content;

	private final BigDecimal ratio;

	private final LocalDateTime updatedAt;

	private PlaceReviewUpdateResponseDto(PlaceReview placeReview) {
		this.placeId = placeReview.getPlace().getId();
		this.nickName = placeReview.getUser().getNickname();
		this.id = placeReview.getId();
		this.content = placeReview.getContent();
		this.ratio = placeReview.getRatio();
		this.updatedAt = placeReview.getUpdatedAt();
	}

	public static PlaceReviewUpdateResponseDto from(PlaceReview placeReview) {
		return new PlaceReviewUpdateResponseDto(placeReview);
	}
}
