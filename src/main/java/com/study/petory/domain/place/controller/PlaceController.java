package com.study.petory.domain.place.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.study.petory.common.response.CommonResponse;
import com.study.petory.domain.place.dto.request.PlaceCreateRequestDto;
import com.study.petory.domain.place.dto.response.PlaceCreateResponseDto;
import com.study.petory.domain.place.service.PlaceService;
import com.study.petory.exception.enums.SuccessCode;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/places")
public class PlaceController {

	private final PlaceService placeService;

	/**
	 * 장소 등록
	 * 사진, 평균 평점 추가 예정
	 * @param requestDto 장소 등록에 필요한 정보
	 * @return CommonResponse 방식의 등록된 장소 정보
	 */
	@PostMapping
	public CommonResponse<PlaceCreateResponseDto> createPlace(
		@RequestBody PlaceCreateRequestDto requestDto
	) {
		return CommonResponse.of(SuccessCode.CREATED, placeService.savePlace(requestDto));
	}

}
