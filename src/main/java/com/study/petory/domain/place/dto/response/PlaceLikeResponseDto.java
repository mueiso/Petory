package com.study.petory.domain.place.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PlaceLikeResponseDto {

	private final Long placeId;

	private final Long userId;

	private final Long likeCount;

	private final boolean isLiked;

	@Builder
	public PlaceLikeResponseDto(Long placeId, Long userId, Long likeCount, boolean isLiked) {
		this.placeId = placeId;
		this.userId = userId;
		this.likeCount = likeCount;
		this.isLiked = isLiked;
	}
}
