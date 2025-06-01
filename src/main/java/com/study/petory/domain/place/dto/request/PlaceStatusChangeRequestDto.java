package com.study.petory.domain.place.dto.request;

import com.study.petory.domain.place.entity.PlaceStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PlaceStatusChangeRequestDto {

	private final PlaceStatus placeStatus;
}
