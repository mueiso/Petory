package com.study.petory.domain.place.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PlaceReportCancelRequestDto {

	private final String reason;
}
