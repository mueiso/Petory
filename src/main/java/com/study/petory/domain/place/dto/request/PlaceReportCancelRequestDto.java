package com.study.petory.domain.place.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PlaceReportCancelRequestDto {

	@Size(max = 30, message = "사유는 30자까지만 입력 가능합니다.")
	private final String reason;
}
