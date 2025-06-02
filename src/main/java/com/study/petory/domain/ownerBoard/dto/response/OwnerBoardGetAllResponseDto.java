package com.study.petory.domain.ownerBoard.dto.response;

import com.study.petory.domain.ownerBoard.entity.OwnerBoard;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OwnerBoardGetAllResponseDto {

	private final Long id;

	private final String title;

	private final String content;

	public static OwnerBoardGetAllResponseDto from(OwnerBoard ownerBoard) {
		return new OwnerBoardGetAllResponseDto(
			ownerBoard.getId(),
			ownerBoard.getTitle(),
			ownerBoard.getContent()
		);
	}
}
