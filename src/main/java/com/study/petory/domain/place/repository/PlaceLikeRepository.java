package com.study.petory.domain.place.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.study.petory.domain.place.entity.PlaceLike;
import com.study.petory.domain.user.entity.User;

public interface PlaceLikeRepository extends JpaRepository<PlaceLike, Long> {

	List<PlaceLike> findByUser(User user);

	boolean existsByPlaceIdAndUserId(Long placeId, Long userId);

	void deleteByPlaceIdAndUserId(Long placeId, Long userId);
}
