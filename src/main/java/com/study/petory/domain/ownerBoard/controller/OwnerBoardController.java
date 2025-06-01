package com.study.petory.domain.ownerBoard.controller;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.study.petory.common.response.CommonResponse;
import com.study.petory.domain.ownerBoard.dto.request.OwnerBoardCreateRequestDto;
import com.study.petory.domain.ownerBoard.dto.request.OwnerBoardUpdateRequestDto;
import com.study.petory.domain.ownerBoard.dto.response.OwnerBoardCreateResponseDto;
import com.study.petory.domain.ownerBoard.dto.response.OwnerBoardGetAllResponseDto;
import com.study.petory.domain.ownerBoard.dto.response.OwnerBoardGetResponseDto;
import com.study.petory.domain.ownerBoard.dto.response.OwnerBoardUpdateResponseDto;
import com.study.petory.domain.ownerBoard.service.OwnerBoardService;
import com.study.petory.exception.enums.SuccessCode;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/owner-boards")
public class OwnerBoardController {

	private final OwnerBoardService ownerBoardService;

	/**
	 * 게시글 생성
	 * @param dto 제목, 내용 //사진 추가 예정
	 * @return id, 제목, 내용, 생성일
	 */
	@PostMapping
	public CommonResponse<OwnerBoardCreateResponseDto> createOwnerBoard(
		@Valid @RequestBody OwnerBoardCreateRequestDto dto) {

		return CommonResponse.of(SuccessCode.CREATED, ownerBoardService.saveOwnerBoard(dto));
	}

	/**
	 * 게시글 전체 조회
	 * @param title 제목 검색 가능(nullable)
	 * @param page 조회하려는 페이지 위치
	 * @return List형식의 게시글 반환
	 */
	@GetMapping
	public CommonResponse<Page<OwnerBoardGetAllResponseDto>> getOwnerBoardsAll(
		@RequestParam(required = false) String title,
		@RequestParam(defaultValue = "1") int page) {

		return CommonResponse.of(SuccessCode.OK, ownerBoardService.findAllOwnerBoards(title, page));
	}

	/**
	 * 게시글 단건 조회
	 * @param boardId 주인커뮤니티 게시글 ID
	 * @return 게시글 id,제목,내용,작성일,수정일 //사진 및 댓글리스트 추가 예정
	 */
	@GetMapping("/{boardId}")
	public CommonResponse<OwnerBoardGetResponseDto> getOwnerBoard(@PathVariable Long boardId) {

		return CommonResponse.of(SuccessCode.OK, ownerBoardService.findOwnerBoard(boardId));
	}

	/**
	 * 게시글 수정
	 * @param boardId 수정할 주인커뮤니티 게시글 ID
	 * @param dto 제목, 내용 // 사진 추가 예정
	 * @return 수정된 OwnerBoard 반환
	 */
	@PatchMapping("/{boardId}")
	public CommonResponse<OwnerBoardUpdateResponseDto> updateOwnerBoard(
		@PathVariable Long boardId,
		@Valid @RequestBody OwnerBoardUpdateRequestDto dto) {

		return CommonResponse.of(SuccessCode.OK, ownerBoardService.updateOwnerBoard(boardId, dto));
	}

	/**
	 * 게시글 삭제
	 * @param boardId 삭제할 게시글 ID
	 * @return no_content status 반환
	 */
	@DeleteMapping("/{boardId}")
	public CommonResponse<Void> deleteOwnerBoard(@PathVariable Long boardId) {
		ownerBoardService.deleteOwnerBoard(boardId);

		return CommonResponse.of(SuccessCode.NO_CONTENT);
	}
}
