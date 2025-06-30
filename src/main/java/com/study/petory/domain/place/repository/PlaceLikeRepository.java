package com.study.petory.domain.place.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.study.petory.domain.place.entity.PlaceLike;
import com.study.petory.domain.user.entity.User;

public interface PlaceLikeRepository extends JpaRepository<PlaceLike, Long> {

	boolean existsByPlace_IdAndUser_Id(Long placeId, Long userId);

	void deleteByPlace_IdAndUser_Id(Long placeId, Long userId);

	List<PlaceLike> findByUser(User user);
}
