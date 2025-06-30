package com.study.petory.domain.ownerBoard.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.study.petory.domain.ownerBoard.entity.OwnerBoardComment;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OwnerBoardCommentGetResponseDto {

	private final Long commentId;
	private final String content;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private final LocalDateTime createdAt;
	private final Long writerId;

	public static OwnerBoardCommentGetResponseDto from(OwnerBoardComment comment) {
		return new OwnerBoardCommentGetResponseDto(
			comment.getId(),
			comment.getContent(),
			comment.getCreatedAt(),
			comment.getUser() != null ? comment.getUser().getId() : null  // User 가 null 일 경우 null 로 노출되도록
		);
	}
}
