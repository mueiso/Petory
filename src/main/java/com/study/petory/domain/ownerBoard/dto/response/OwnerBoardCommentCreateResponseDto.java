package com.study.petory.domain.ownerBoard.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.study.petory.domain.ownerBoard.entity.OwnerBoardComment;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OwnerBoardCommentCreateResponseDto {

	private final Long id;
	private final String content;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private final LocalDateTime createdAt;
	private final Long writerId;

	public static OwnerBoardCommentCreateResponseDto from(OwnerBoardComment comment) {
		return new OwnerBoardCommentCreateResponseDto(
			comment.getId(),
			comment.getContent(),
			comment.getCreatedAt(),
			comment.getUser().getId()
		);
	}
}
