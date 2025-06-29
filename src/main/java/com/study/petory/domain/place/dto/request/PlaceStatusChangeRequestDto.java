package com.study.petory.domain.place.dto.request;

import com.study.petory.common.util.ValidEnum;
import com.study.petory.domain.place.entity.PlaceStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PlaceStatusChangeRequestDto {

	@ValidEnum(enumClass = PlaceStatus.class, message = "지원하지 않는 상태값입니다.")
	private final PlaceStatus placeStatus;
}
