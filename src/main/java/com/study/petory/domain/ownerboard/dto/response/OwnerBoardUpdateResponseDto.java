package com.study.petory.domain.ownerboard.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.study.petory.domain.ownerboard.entity.OwnerBoard;
import com.study.petory.domain.ownerboard.entity.OwnerBoardImage;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OwnerBoardUpdateResponseDto {

	private final Long id;

	private final String title;

	private final String content;

	private final List<OwnerBoardImage> images;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private final LocalDateTime updatedAt;

	public static OwnerBoardUpdateResponseDto from(OwnerBoard ownerBoard) {
		return new OwnerBoardUpdateResponseDto(
			ownerBoard.getId(),
			ownerBoard.getTitle(),
			ownerBoard.getContent(),
			ownerBoard.getImages(),
			ownerBoard.getUpdatedAt()
		);
	}

}

