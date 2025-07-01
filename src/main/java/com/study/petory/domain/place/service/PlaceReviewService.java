package com.study.petory.domain.place.service;

import java.util.List;

import com.study.petory.domain.place.dto.request.PlaceReviewCreateRequestDto;
import com.study.petory.domain.place.dto.request.PlaceReviewUpdateRequestDto;
import com.study.petory.domain.place.dto.response.PlaceReviewCreateResponseDto;
import com.study.petory.domain.place.dto.response.PlaceReviewUpdateResponseDto;
import com.study.petory.domain.place.entity.PlaceReview;

public interface PlaceReviewService {
	PlaceReviewCreateResponseDto savePlaceReview(Long userId, Long placeId, PlaceReviewCreateRequestDto requestDto);

	PlaceReviewUpdateResponseDto updatePlaceReview(Long userId, Long placeId, Long reviewId,
		PlaceReviewUpdateRequestDto requestDto);

	void restorePlaceReview(Long userId, Long placeId, Long reviewId);

	void deletePlaceReview(Long userId, Long placeId, Long reviewId);

	PlaceReview findPlaceReviewByReviewId(Long PlaceReviewId);

	List<PlaceReview> findPlaceReviewListByPlaceId(Long placeId);
}
