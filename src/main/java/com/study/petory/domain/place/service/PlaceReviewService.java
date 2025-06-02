package com.study.petory.domain.place.service;

import com.study.petory.domain.place.dto.request.PlaceReviewCreateRequestDto;
import com.study.petory.domain.place.dto.request.PlaceReviewUpdateRequestDto;
import com.study.petory.domain.place.dto.response.PlaceReviewCreateResponseDto;
import com.study.petory.domain.place.dto.response.PlaceReviewUpdateResponseDto;
import com.study.petory.domain.place.entity.PlaceReview;

import jakarta.validation.Valid;

public interface PlaceReviewService {
	PlaceReviewCreateResponseDto savePlaceReview(Long placeId, PlaceReviewCreateRequestDto requestDto);

	PlaceReviewUpdateResponseDto updatePlaceReview(Long placeId, Long reviewId, PlaceReviewUpdateRequestDto requestDto);

	void restorePlaceReview(Long placeId, Long reviewId);

	void deletePlaceReview(Long placeId, Long reviewId);

	PlaceReview findPlaceReviewByReviewIdOrElseThrow(Long PlaceReviewId);
}
