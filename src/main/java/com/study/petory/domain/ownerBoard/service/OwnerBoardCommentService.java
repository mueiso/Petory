package com.study.petory.domain.ownerBoard.service;

import org.springframework.data.domain.Page;

import com.study.petory.domain.ownerBoard.dto.request.OwnerBoardCommentCreateRequestDto;
import com.study.petory.domain.ownerBoard.dto.request.OwnerBoardCommentUpdateRequestDto;
import com.study.petory.domain.ownerBoard.dto.response.OwnerBoardCommentCreateResponseDto;
import com.study.petory.domain.ownerBoard.dto.response.OwnerBoardCommentGetResponseDto;
import com.study.petory.domain.ownerBoard.dto.response.OwnerBoardCommentUpdateResponseDto;
import com.study.petory.domain.ownerBoard.entity.OwnerBoardComment;

public interface OwnerBoardCommentService {
	OwnerBoardComment findOwnerBoardCommentById(Long commentId);

	OwnerBoardCommentCreateResponseDto saveOwnerBoardComment(Long boardId, OwnerBoardCommentCreateRequestDto dto);

	Page<OwnerBoardCommentGetResponseDto> findAllOwnerBoardComments(Long boardId, int page);

	OwnerBoardCommentUpdateResponseDto updateOwnerBoardComment(Long boardId, Long commentId,
		OwnerBoardCommentUpdateRequestDto dto);

	void deleteOwnerBoardComment(Long boardId, Long commentId);
}
