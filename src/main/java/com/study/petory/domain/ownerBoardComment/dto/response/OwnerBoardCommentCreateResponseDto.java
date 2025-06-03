package com.study.petory.domain.ownerBoardComment.dto.response;

import java.time.LocalDateTime;

import com.study.petory.domain.ownerBoardComment.entity.OwnerBoardComment;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OwnerBoardCommentCreateResponseDto {

	private final Long id;
	private final String content;
	private final LocalDateTime createdAt;
	private final String nickname;

	public static OwnerBoardCommentCreateResponseDto from(OwnerBoardComment comment) {
		return new OwnerBoardCommentCreateResponseDto(
			comment.getId(),
			comment.getContent(),
			comment.getCreatedAt(),
			comment.getUser().getNickname()
		);
	}
}
