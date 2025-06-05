package com.study.petory.domain.place.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.study.petory.domain.place.dto.response.PlaceGetAllResponseDto;
import com.study.petory.domain.place.entity.Place;
import com.study.petory.domain.place.entity.PlaceType;

public interface PlaceRepository extends JpaRepository<Place, Long> {

	// placeName, placeType이 둘 다 있는 경우. 두 가지의 필터를 모두 포함한 조회
	Page<PlaceGetAllResponseDto> findAllByPlaceNameContainingAndPlaceType(String placeName, PlaceType placeType,
		Pageable pageable);

	// placeName이 존재하는 경우 placeName 중 일부만 입력되는 경우에도 조회 가능
	Page<PlaceGetAllResponseDto> findAllByPlaceNameContaining(String placeName, Pageable pageable);

	// placeType이 존재하는 경우 placeType 기준 조회
	Page<PlaceGetAllResponseDto> findAllByPlaceType(PlaceType placeType, Pageable pageable);

	// 전체 조회
	@Query(
		"SELECT new com.study.petory.domain.place.dto.response.PlaceGetAllResponseDto(p.id, p.placeName, p.placeInfo, p.placeType, p.ratio, p.latitude, p.longitude) "
			+ "FROM Place p")
	Page<PlaceGetAllResponseDto> findAllPlace(Pageable pageable);

	// 특정 조회 - 리뷰 리스트까지 함께 조회하기 위한 메서드
	@EntityGraph(attributePaths = {"placeReviewList", "user"})
	Optional<Place> findWithReviewsById(Long id);
}
