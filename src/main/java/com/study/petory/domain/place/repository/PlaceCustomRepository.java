package com.study.petory.domain.place.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.study.petory.domain.place.dto.response.PlaceGetAllResponseDto;
import com.study.petory.domain.place.entity.Place;
import com.study.petory.domain.place.entity.PlaceType;

public interface PlaceCustomRepository {
	Page<PlaceGetAllResponseDto> findAllPlace(String placeName, PlaceType placeType, String address, Pageable pageable);

	// 특정 조회 - 리뷰 리스트까지 함께 조회하기 위한 메서드
	Optional<Place> findWithReviewListById(Long id);
}
