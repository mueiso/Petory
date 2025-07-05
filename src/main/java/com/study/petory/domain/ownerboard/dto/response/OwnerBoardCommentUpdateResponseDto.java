package com.study.petory.domain.ownerboard.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.study.petory.domain.ownerboard.entity.OwnerBoardComment;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OwnerBoardCommentUpdateResponseDto {

	private final Long id;
	private final String content;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private final LocalDateTime updatedAt;
	private final Long writerId;

	public static OwnerBoardCommentUpdateResponseDto from(OwnerBoardComment comment) {
		return new OwnerBoardCommentUpdateResponseDto(
			comment.getId(),
			comment.getContent(),
			comment.getUpdatedAt(),
			comment.getUser().getId()
		);
	}
}
