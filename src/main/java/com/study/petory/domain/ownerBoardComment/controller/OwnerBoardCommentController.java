package com.study.petory.domain.ownerBoardComment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.study.petory.common.response.CommonResponse;
import com.study.petory.domain.ownerBoardComment.dto.request.OwnerBoardCommentRequestDto;
import com.study.petory.domain.ownerBoardComment.dto.response.OwnerBoardCommentCreateResponseDto;
import com.study.petory.domain.ownerBoardComment.service.OwnerBoardCommentService;
import com.study.petory.exception.enums.SuccessCode;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/owner-boards")
public class OwnerBoardCommentController {

	private final OwnerBoardCommentService ownerBoardCommentService;

	/**
	 * 주인커뮤니티 댓글 생성
	 * @param boardId 게시글 Id
	 * @param dto 내용 작성
	 * @return id, 내용, 생성일, 작성자 닉네임 반환
	 */
	@PostMapping("/{boardId}/comments")
	public ResponseEntity<CommonResponse<OwnerBoardCommentCreateResponseDto>> createOwnerBoardComment(
		@PathVariable Long boardId,
		@Valid @RequestBody OwnerBoardCommentRequestDto dto) {

		return CommonResponse.of(SuccessCode.CREATED, ownerBoardCommentService.saveOwnerBoardComment(boardId, dto));
	}


	//댓글 수정
	//댓글 삭제

}
