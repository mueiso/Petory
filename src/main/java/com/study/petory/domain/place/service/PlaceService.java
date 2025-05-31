package com.study.petory.domain.place.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.study.petory.domain.place.dto.request.PlaceCreateRequestDto;
import com.study.petory.domain.place.dto.response.PlaceCreateResponseDto;
import com.study.petory.domain.place.dto.response.PlaceGetResponseDto;
import com.study.petory.domain.place.entity.PlaceType;

public interface PlaceService {
	PlaceCreateResponseDto savePlace(PlaceCreateRequestDto requestDto);

	Page<PlaceGetResponseDto> findAllPlace(String placeName, PlaceType placeType, Pageable pageable);
}
