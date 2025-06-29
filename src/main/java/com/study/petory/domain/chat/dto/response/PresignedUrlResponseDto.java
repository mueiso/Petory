package com.study.petory.domain.chat.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PresignedUrlResponseDto {

	private final String uploadUrl;

	private final String fileUrl;
}
