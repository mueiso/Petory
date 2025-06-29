package com.study.petory.domain.place.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.study.petory.domain.place.dto.request.PlaceCreateRequestDto;
import com.study.petory.domain.place.dto.request.PlaceStatusChangeRequestDto;
import com.study.petory.domain.place.dto.request.PlaceUpdateRequestDto;
import com.study.petory.domain.place.dto.response.PlaceCreateResponseDto;
import com.study.petory.domain.place.dto.response.PlaceGetAllResponseDto;
import com.study.petory.domain.place.dto.response.PlaceGetResponseDto;
import com.study.petory.domain.place.dto.response.PlaceUpdateResponseDto;
import com.study.petory.domain.place.entity.Place;
import com.study.petory.domain.place.entity.PlaceType;

public interface PlaceService {
	PlaceCreateResponseDto savePlace(Long userId, PlaceCreateRequestDto requestDto);

	Page<PlaceGetAllResponseDto> findAllPlace(String placeName, PlaceType placeType, String address, Pageable pageable);

	PlaceGetResponseDto findByPlaceId(Long placeId);

	PlaceUpdateResponseDto updatePlace(Long userId, Long placeId, PlaceUpdateRequestDto requestDto);

	void deletePlace(Long placeId, PlaceStatusChangeRequestDto requestDto);

	void restorePlace(Long placeId, PlaceStatusChangeRequestDto requestDto);

	Place findPlaceByPlaceId(Long placeId);

	Place findPlaceWithPlaceReviewByPlaceId(Long placeId);

	List<PlaceGetAllResponseDto> findPlaceRank(PlaceType placeType);

	String makeKey(PlaceType placeType);

	PlaceType parsePlaceType(String placeType);
}
