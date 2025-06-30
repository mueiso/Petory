package com.study.petory.domain.place.dto.request;

import java.math.BigDecimal;

import com.study.petory.common.util.ValidEnum;
import com.study.petory.domain.place.entity.PlaceType;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PlaceCreateRequestDto {

	@NotBlank
	@Size(max = 30, message = "장소 이름은 30자까지만 입력 가능합니다.")
	private final String placeName;

	@Size(max = 255, message = "장소 정보는 255자까지만 입력 가능합니다.")
	private final String placeInfo;

	@ValidEnum(enumClass = PlaceType.class, ignoreCase = true, message = "지원하지 않는 장소 타입입니다.")
	private final String placeType;

	@NotBlank
	@Size(max = 100, message = "주소는 100자까지만 입력 가능합니다.")
	private final String address;

	@Digits(integer = 9, fraction = 6)
	private final BigDecimal latitude;

	@Digits(integer = 10, fraction = 6)
	private final BigDecimal longitude;
}
