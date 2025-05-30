package com.study.petory.domain.ownerBoard.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OwnerBoardCreateResponseDto {

	private final Long id;

	private final String title;

	private final String content;

	// 사진 여러개 받기 가능
	private final List<String> photoUrls;

	private final LocalDateTime createdAt;
}
