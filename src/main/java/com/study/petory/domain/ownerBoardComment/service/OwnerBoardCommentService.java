package com.study.petory.domain.ownerBoardComment.service;

import com.study.petory.domain.ownerBoardComment.dto.request.OwnerBoardCommentRequestDto;
import com.study.petory.domain.ownerBoardComment.dto.response.OwnerBoardCommentCreateResponseDto;

public interface OwnerBoardCommentService {
	OwnerBoardCommentCreateResponseDto saveOwnerBoardComment(Long boardId, OwnerBoardCommentRequestDto dto);
}
