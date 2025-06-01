package com.study.petory.domain.place.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.study.petory.common.util.EntityFetcher;
import com.study.petory.domain.place.dto.request.PlaceReviewCreateRequestDto;
import com.study.petory.domain.place.dto.response.PlaceReviewCreateResponseDto;
import com.study.petory.domain.place.entity.Place;
import com.study.petory.domain.place.entity.PlaceReview;
import com.study.petory.domain.place.repository.PlaceReviewRepository;
import com.study.petory.domain.user.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlaceReviewServiceImpl implements PlaceReviewService{

	private final PlaceReviewRepository placeReviewRepository;
	private final EntityFetcher entityFetcher;

	@Override
	@Transactional
	public PlaceReviewCreateResponseDto savePlaceReview(Long placeId, PlaceReviewCreateRequestDto requestDto) {

		Place findPlace = entityFetcher.findPlaceByPlaceId(placeId);
		User findUser = entityFetcher.findUserByUserId(1L);

		PlaceReview placeReview = PlaceReview.builder()
			.place(findPlace)
			.user(findUser)
			.content(requestDto.getContent())
			.ratio(requestDto.getRatio())
			.build();

		placeReviewRepository.save(placeReview);

		return PlaceReviewCreateResponseDto.from(placeReview);
	}
}
