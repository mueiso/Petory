package com.study.petory.domain.place.service;

import com.study.petory.domain.place.dto.request.PlaceCreateRequestDto;
import com.study.petory.domain.place.dto.response.PlaceCreateResponseDto;

public interface PlaceService {
	PlaceCreateResponseDto savePlace(PlaceCreateRequestDto requestDto);
}
