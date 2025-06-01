package com.study.petory.domain.place.service;

import com.study.petory.domain.place.dto.request.PlaceReviewCreateRequestDto;
import com.study.petory.domain.place.dto.response.PlaceReviewCreateResponseDto;

import jakarta.validation.Valid;

public interface PlaceReviewService {
	PlaceReviewCreateResponseDto savePlaceReview(Long placeId, PlaceReviewCreateRequestDto requestDto);
}
