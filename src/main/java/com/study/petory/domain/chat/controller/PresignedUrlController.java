package com.study.petory.domain.chat.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.study.petory.common.exception.enums.SuccessCode;
import com.study.petory.common.response.CommonResponse;
import com.study.petory.domain.chat.dto.request.PresignedUrlRequestDto;
import com.study.petory.domain.chat.dto.response.PresignedUrlResponseDto;
import com.study.petory.domain.chat.service.PresignedUrlService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PresignedUrlController {

	private final PresignedUrlService presignedUrlService;

	@PostMapping("/chat/image")
	public ResponseEntity<CommonResponse<PresignedUrlResponseDto>> getPresignedUrl(
		@RequestBody PresignedUrlRequestDto requestDto
	) {
		return CommonResponse.of(SuccessCode.CREATED, presignedUrlService.createPresignedUrl(requestDto));
	}
}
