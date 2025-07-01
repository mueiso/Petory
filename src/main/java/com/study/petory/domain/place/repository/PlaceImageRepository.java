package com.study.petory.domain.place.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.study.petory.domain.place.entity.PlaceImage;

public interface PlaceImageRepository extends JpaRepository<PlaceImage, Long> {

	List<PlaceImage> findAllByPlaceId(Long placeId);
}
