package com.study.petory.domain.ownerBoard.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OwnerBoardGetAllResponseDto {

	private final Long id;

	private final String title;

	private final String content;

	private final String imageUrl;

}
