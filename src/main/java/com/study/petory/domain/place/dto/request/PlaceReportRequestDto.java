package com.study.petory.domain.place.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PlaceReportRequestDto {

	@NotBlank
	@Size(max = 255, message = "신고 이유는 255자까지만 입력 가능합니다.")
	private final String content;
}
