package com.study.petory.domain.place.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.study.petory.common.exception.CustomException;
import com.study.petory.common.exception.enums.ErrorCode;
import com.study.petory.domain.place.dto.request.PlaceReviewCreateRequestDto;
import com.study.petory.domain.place.dto.request.PlaceReviewUpdateRequestDto;
import com.study.petory.domain.place.dto.response.PlaceReviewCreateResponseDto;
import com.study.petory.domain.place.dto.response.PlaceReviewUpdateResponseDto;
import com.study.petory.domain.place.entity.Place;
import com.study.petory.domain.place.entity.PlaceReview;
import com.study.petory.domain.place.repository.PlaceReviewRepository;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlaceReviewServiceImpl implements PlaceReviewService {

	private final PlaceReviewRepository placeReviewRepository;
	private final PlaceService placeService;
	private final UserService userService;

	// 리뷰 등록
	@Override
	@Transactional
	public PlaceReviewCreateResponseDto savePlaceReview(Long userId, Long placeId,
		PlaceReviewCreateRequestDto requestDto) {

		Place findPlace = placeService.findPlaceWithPlaceReviewByPlaceId(placeId);
		User findUser = userService.getUserById(userId);

		// 한 유저가 같은 장소에 한 개의 리뷰만 등록할 수 있도록 검증하는 로직
		if (placeReviewRepository.existsByUserAndPlace(findUser, findPlace)) {
			throw new CustomException(ErrorCode.DUPLICATE_REVIEW);
		}

		PlaceReview placeReview = PlaceReview.builder()
			.place(findPlace)
			.user(findUser)
			.content(requestDto.getContent())
			.ratio(requestDto.getRatio())
			.build();

		placeReviewRepository.save(placeReview);

		// 평균 평점 로직
		findPlace.updateRatio(calculateAvgRatio(placeId));

		return PlaceReviewCreateResponseDto.from(placeReview);
	}

	// 리뷰 수정
	@Override
	@Transactional
	public PlaceReviewUpdateResponseDto updatePlaceReview(Long userId, Long placeId, Long reviewId,
		PlaceReviewUpdateRequestDto requestDto) {

		// 해당 장소가 존재하는지 검증하기 위한 로직
		Place findPlace = placeService.findPlaceWithPlaceReviewByPlaceId(placeId);

		PlaceReview findPlaceReview = findPlaceReviewByReviewId(reviewId);

		verifyAuthorEdit(findPlaceReview, userId);

		findPlaceReview.updatePlaceReview(requestDto.getContent(), requestDto.getRatio());

		// 평균 평점 로직
		findPlace.updateRatio(calculateAvgRatio(placeId));

		return PlaceReviewUpdateResponseDto.from(findPlaceReview);
	}

	// 리뷰 복구
	@Override
	@Transactional
	public void restorePlaceReview(Long userId, Long placeId, Long reviewId) {

		// 해당 장소가 존재하는지 검증하기 위한 로직
		Place findPlace = placeService.findPlaceWithPlaceReviewByPlaceId(placeId);

		PlaceReview findPlaceReview = findPlaceReviewByReviewId(reviewId);

		// deletedAt이 null 이라면 즉, 삭제되지 않았다면 복구가 안되므로 그것에 관한 검증 로직
		if (findPlaceReview.isDeletedAtNull()) {
			throw new CustomException(ErrorCode.REVIEW_NOT_DELETED);
		}

		findPlaceReview.restoreEntity();

		// 평균 평점 로직
		findPlace.updateRatio(calculateAvgRatio(placeId));
	}

	// 리뷰 삭제
	@Override
	@Transactional
	public void deletePlaceReview(Long userId, Long placeId, Long reviewId) {

		// 해당 장소가 존재하는지 검증하기 위한 로직
		Place findPlace = placeService.findPlaceWithPlaceReviewByPlaceId(placeId);

		PlaceReview findPlaceReview = findPlaceReviewByReviewId(reviewId);

		verifyAuthorDelete(findPlaceReview, userId);

		// deletedAt이 null 이 아니라면 즉, 삭제되었다면 삭제가 안되므로 그것에 관한 검증 로직
		if (!findPlaceReview.isDeletedAtNull()) {
			throw new CustomException(ErrorCode.ALREADY_DELETED_REVIEW);
		}

		findPlaceReview.deactivateEntity();

		// 평균 평점 로직
		findPlace.updateRatio(calculateAvgRatio(placeId));
	}

	// 다른 서비스에서 사용가능하게 설정한 메서드
	// throws CustomException
	@Override
	public PlaceReview findPlaceReviewByReviewId(Long placeReviewId) {
		return placeReviewRepository.findById(placeReviewId)
			.orElseThrow(() -> new CustomException(ErrorCode.PLACE_REVIEW_NOT_FOUND));
	}

	private void verifyAuthorEdit(PlaceReview placeReview, Long userId) {
		if (!placeReview.isEqualUser(userId)) {
			throw new CustomException(ErrorCode.ONLY_AUTHOR_CAN_EDIT);
		}
	}

	private void verifyAuthorDelete(PlaceReview placeReview, Long userId) {
		if (!placeReview.isEqualUser(userId)) {
			throw new CustomException(ErrorCode.ONLY_AUTHOR_CAN_DELETE);
		}
	}

	private BigDecimal calculateAvgRatio(Long placeId) {
		Double doubleAvg = placeReviewRepository.calculateAvgRatioByPlaceId(placeId);

		if(doubleAvg == null) {
			doubleAvg = 0.0;
		}

		return BigDecimal.valueOf(doubleAvg)
			.setScale(1, RoundingMode.HALF_UP);
	}
}
