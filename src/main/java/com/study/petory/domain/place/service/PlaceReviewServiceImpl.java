package com.study.petory.domain.place.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.study.petory.common.util.EntityFetcher;
import com.study.petory.domain.place.dto.request.PlaceReviewCreateRequestDto;
import com.study.petory.domain.place.dto.request.PlaceReviewUpdateRequestDto;
import com.study.petory.domain.place.dto.response.PlaceReviewCreateResponseDto;
import com.study.petory.domain.place.dto.response.PlaceReviewUpdateResponseDto;
import com.study.petory.domain.place.entity.Place;
import com.study.petory.domain.place.entity.PlaceReview;
import com.study.petory.domain.place.repository.PlaceReviewRepository;
import com.study.petory.domain.user.entity.User;
import com.study.petory.exception.CustomException;
import com.study.petory.exception.enums.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlaceReviewServiceImpl implements PlaceReviewService{

	private final PlaceReviewRepository placeReviewRepository;
	private final EntityFetcher entityFetcher;

	// 리뷰 등록
	@Override
	@Transactional
	public PlaceReviewCreateResponseDto savePlaceReview(Long placeId, PlaceReviewCreateRequestDto requestDto) {

		Place findPlace = entityFetcher.findPlaceByPlaceId(placeId);
		User findUser = entityFetcher.findUserByUserId(1L);

		// 한 유저가 같은 장소에 한 개의 리뷰만 등록할 수 있도록 검증하는 로직
		Optional<PlaceReview> findPlaceReview = placeReviewRepository.findByUserAndPlace(findUser, findPlace);

		if(findPlaceReview.isPresent()) {
			throw new CustomException(ErrorCode.DUPLICATE_REVIEW);
		}

		PlaceReview placeReview = PlaceReview.builder()
			.place(findPlace)
			.user(findUser)
			.content(requestDto.getContent())
			.ratio(requestDto.getRatio())
			.build();

		placeReviewRepository.save(placeReview);

		return PlaceReviewCreateResponseDto.from(placeReview);
	}

	// 리뷰 수정
	@Override
	@Transactional
	public PlaceReviewUpdateResponseDto updatePlaceReview(Long placeId, Long reviewId,
		PlaceReviewUpdateRequestDto requestDto) {

		// 해당 장소가 존재하는지 검증하기 위한 로직
		entityFetcher.findPlaceByPlaceId(placeId);

		PlaceReview findPlaceReview = placeReviewRepository.findByIdOrElseThrow(reviewId);

		findPlaceReview.updatePlaceReview(requestDto);

		return PlaceReviewUpdateResponseDto.from(findPlaceReview);
	}
}
