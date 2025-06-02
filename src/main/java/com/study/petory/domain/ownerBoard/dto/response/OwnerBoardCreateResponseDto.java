package com.study.petory.domain.ownerBoard.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.study.petory.domain.ownerBoard.entity.OwnerBoard;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OwnerBoardCreateResponseDto {

	private final Long id;

	private final String title;

	private final String content;

	// photoUrlList 추가 예정

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private final LocalDateTime createdAt;

	public static OwnerBoardCreateResponseDto from(OwnerBoard ownerBoard) {
		return new OwnerBoardCreateResponseDto(
			ownerBoard.getId(),
			ownerBoard.getTitle(),
			ownerBoard.getContent(),
			ownerBoard.getCreatedAt()
		);
	}

}

