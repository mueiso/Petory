package com.study.petory.domain.place.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.study.petory.domain.place.entity.Place;
import com.study.petory.domain.place.entity.PlaceReview;
import com.study.petory.domain.user.entity.User;
import com.study.petory.exception.CustomException;
import com.study.petory.exception.enums.ErrorCode;

public interface PlaceReviewRepository extends JpaRepository<PlaceReview, Long> {

	default PlaceReview findByIdOrElseThrow(Long reviewId) {
		return findById(reviewId).orElseThrow(() -> new CustomException(ErrorCode.PLACE_REVIEW_NOT_FOUND));
	}

	Optional<PlaceReview> findByUserAndPlace(User user, Place place);
}
