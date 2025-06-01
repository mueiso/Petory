package com.study.petory.domain.ownerBoard.dto.response;

import java.time.LocalDateTime;

import com.study.petory.domain.ownerBoard.entity.OwnerBoard;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OwnerBoardGetResponseDto {

	private final Long id;

	private final String title;

	private final String content;

	// commentList 추가 예정

	private final LocalDateTime createdAt;

	private final LocalDateTime updatedAt;

	public static OwnerBoardGetResponseDto from(OwnerBoard ownerBoard) {
		return new OwnerBoardGetResponseDto(
			ownerBoard.getId(),
			ownerBoard.getTitle(),
			ownerBoard.getContent(),
			ownerBoard.getCreatedAt(),
			ownerBoard.getUpdatedAt()
		);
	}
}
