package com.study.petory.domain.ownerBoardComment.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.study.petory.common.response.CommonResponse;
import com.study.petory.domain.ownerBoardComment.dto.request.OwnerBoardCommentCreateRequestDto;
import com.study.petory.domain.ownerBoardComment.dto.request.OwnerBoardCommentUpdateRequestDto;
import com.study.petory.domain.ownerBoardComment.dto.response.OwnerBoardCommentCreateResponseDto;
import com.study.petory.domain.ownerBoardComment.dto.response.OwnerBoardCommentGetResponseDto;
import com.study.petory.domain.ownerBoardComment.dto.response.OwnerBoardCommentUpdateResponseDto;
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
	 * @return 댓글 Id, 내용, 생성일, 작성자 ID 반환
	 */
	@PostMapping("/{boardId}/comments")
	public ResponseEntity<CommonResponse<OwnerBoardCommentCreateResponseDto>> createOwnerBoardComment(
		@PathVariable Long boardId,
		@Valid @RequestBody OwnerBoardCommentCreateRequestDto dto) {

		return CommonResponse.of(SuccessCode.CREATED, ownerBoardCommentService.saveOwnerBoardComment(boardId, dto));
	}

	/**
	 * 게시글의 댓글 전체 조회(페이징)
	 * @param page 댓글 페이지
	 * @return Page size 10, 생성일 기준 오름차순 정렬
	 */
	@GetMapping("/{boardId}/comments")
	public ResponseEntity<CommonResponse<Page<OwnerBoardCommentGetResponseDto>>> getOwnerBoardCommentsAll(
		@RequestParam(defaultValue = "1") int page) {

		return CommonResponse.of(SuccessCode.OK, ownerBoardCommentService.findAllOwnerBoardComments(page));
	}

	/**
	 * 주인커뮤니티 댓글 수정
	 * @param boardId 댓글이 속한 게시글 ID
	 * @param commentId 댓글 ID
	 * @param dto 수정 내용
	 * @return 댓글Id, 수정된 내용, 수정일, 작성자ID
	 */
	@PatchMapping("/{boardId}/comments/{commentId}")
	public ResponseEntity<CommonResponse<OwnerBoardCommentUpdateResponseDto>> updateOwnerBoardComment(
		@PathVariable Long boardId,
		@PathVariable Long commentId,
		@Valid @RequestBody OwnerBoardCommentUpdateRequestDto dto
	) {

		return CommonResponse.of(SuccessCode.OK,
			ownerBoardCommentService.updateOwnerBoardComment(boardId, commentId, dto));
	}

	//댓글 삭제

}
