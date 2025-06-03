package com.study.petory.domain.ownerBoardComment.service;

import org.springframework.data.domain.Page;

import com.study.petory.domain.ownerBoardComment.dto.request.OwnerBoardCommentCreateRequestDto;
import com.study.petory.domain.ownerBoardComment.dto.request.OwnerBoardCommentUpdateRequestDto;
import com.study.petory.domain.ownerBoardComment.dto.response.OwnerBoardCommentCreateResponseDto;
import com.study.petory.domain.ownerBoardComment.dto.response.OwnerBoardCommentGetResponseDto;
import com.study.petory.domain.ownerBoardComment.dto.response.OwnerBoardCommentUpdateResponseDto;
import com.study.petory.domain.ownerBoardComment.entity.OwnerBoardComment;

public interface OwnerBoardCommentService {
	OwnerBoardComment findOwnerBoardCommentById(Long commentId);

	OwnerBoardCommentCreateResponseDto saveOwnerBoardComment(Long boardId, OwnerBoardCommentCreateRequestDto dto);

	Page<OwnerBoardCommentGetResponseDto> findAllOwnerBoardComments(int page);

	OwnerBoardCommentUpdateResponseDto updateOwnerBoardComment(Long boardId, Long commentId,
		OwnerBoardCommentUpdateRequestDto dto);

	void deleteOwnerBoardComment(Long boardId, Long commentId);
}
