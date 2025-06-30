package com.study.petory.domain.ownerBoard.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.study.petory.domain.ownerBoard.entity.OwnerBoard;
import com.study.petory.domain.ownerBoard.entity.OwnerBoardImage;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OwnerBoardGetResponseDto {

	private final Long id;

	private final String title;

	private final String content;

	private final List<OwnerBoardImage> images;

	private final Long writerId;

	// 게시글 단건 조회시 첫 댓글 10개만 가져오기, 11번째부터는 페이징 처리
	private List<OwnerBoardCommentGetResponseDto> commentsList;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private final LocalDateTime createdAt;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private final LocalDateTime updatedAt;

	public static OwnerBoardGetResponseDto of(OwnerBoard ownerBoard,
		List<OwnerBoardCommentGetResponseDto> commentsList) {
		return new OwnerBoardGetResponseDto(
			ownerBoard.getId(),
			ownerBoard.getTitle(),
			ownerBoard.getContent(),
			ownerBoard.getImages(),
			ownerBoard.getUser() != null ? ownerBoard.getUser().getId() : null,  // User 가 null 일 경우 null 로 노출되도록
			commentsList,
			ownerBoard.getCreatedAt(),
			ownerBoard.getUpdatedAt()
		);
	}

}
