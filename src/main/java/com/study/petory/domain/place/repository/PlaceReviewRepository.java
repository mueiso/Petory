package com.study.petory.domain.place.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.study.petory.domain.place.entity.Place;
import com.study.petory.domain.place.entity.PlaceReview;
import com.study.petory.domain.user.entity.User;

public interface PlaceReviewRepository extends JpaRepository<PlaceReview, Long>, PlaceReviewCustomRepository{

	Optional<PlaceReview> findByUserAndPlace(User user, Place place);

	boolean existsByUserAndPlace(User user, Place place);

	List<PlaceReview> findByUser(User user);
}
