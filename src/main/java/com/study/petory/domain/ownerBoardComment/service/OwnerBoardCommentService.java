package com.study.petory.domain.ownerBoardComment.service;

import org.springframework.data.domain.Page;

import com.study.petory.domain.ownerBoardComment.dto.request.OwnerBoardCommentRequestDto;
import com.study.petory.domain.ownerBoardComment.dto.response.OwnerBoardCommentCreateResponseDto;
import com.study.petory.domain.ownerBoardComment.dto.response.OwnerBoardCommentGetResponseDto;

public interface OwnerBoardCommentService {
	OwnerBoardCommentCreateResponseDto saveOwnerBoardComment(Long boardId, OwnerBoardCommentRequestDto dto);

	Page<OwnerBoardCommentGetResponseDto> findAllOwnerBoardComments(int page);
}
