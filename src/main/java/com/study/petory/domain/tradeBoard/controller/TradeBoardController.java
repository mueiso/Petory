package com.study.petory.domain.tradeBoard.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.study.petory.common.exception.enums.SuccessCode;
import com.study.petory.common.response.CommonResponse;
import com.study.petory.common.security.CustomPrincipal;
import com.study.petory.domain.tradeBoard.dto.request.TradeBoardCreateRequestDto;
import com.study.petory.domain.tradeBoard.dto.request.TradeBoardUpdateRequestDto;
import com.study.petory.domain.tradeBoard.dto.response.TradeBoardCreateResponseDto;
import com.study.petory.domain.tradeBoard.dto.response.TradeBoardGetAllResponseDto;
import com.study.petory.domain.tradeBoard.dto.response.TradeBoardGetResponseDto;
import com.study.petory.domain.tradeBoard.dto.response.TradeBoardUpdateResponseDto;
import com.study.petory.domain.tradeBoard.entity.TradeBoardStatus;
import com.study.petory.domain.tradeBoard.entity.TradeCategory;
import com.study.petory.domain.tradeBoard.service.TradeBoardService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/trade-boards")
@RequiredArgsConstructor
public class TradeBoardController {

	private final TradeBoardService tradeBoardService;

	/**
	 * 게시글 등록
	 * @param requestDto 카테고리, 제목, 내용, 사진, 금액
	 * @return id, 카테고리, 제목, 내용, 사진, 금액, 생성일 반환
	 */
	@PostMapping
	private ResponseEntity<CommonResponse<TradeBoardCreateResponseDto>> createTradeBoard(
		@AuthenticationPrincipal CustomPrincipal currentUser,
		@Valid @RequestPart TradeBoardCreateRequestDto requestDto,
		@RequestPart(required = false) List<MultipartFile> images
	) {
		return CommonResponse.of(SuccessCode.CREATED, tradeBoardService.saveTradeBoard(currentUser.getId(), requestDto, images));
	}

	/**
	 * 게시글 전체 조회
	 * @param category 카테고리 (nullable)
	 * @param pageable 조회하려는 페이지
	 * @return 조회된 게시글 반환
	 */
	@GetMapping
	private ResponseEntity<CommonResponse<Page<TradeBoardGetAllResponseDto>>> getAllTradeBoard(
		@RequestParam(required = false) TradeCategory category,
		Pageable pageable
	) {
		return CommonResponse.of(SuccessCode.FOUND, tradeBoardService.findAllTradeBoard(category, pageable));
	}

	/**
	 * 게시글 단건 조회
	 * @param tradeBoardId 조회하려는 게시글 id
	 * @return 해당 게시글 반환
	 */
	@GetMapping("/{tradeBoardId}")
	private ResponseEntity<CommonResponse<TradeBoardGetResponseDto>> getByTradeBoardId(
		@PathVariable Long tradeBoardId
	) {
		return CommonResponse.of(SuccessCode.FOUND, tradeBoardService.findByTradeBoardId(tradeBoardId));
	}

	/**
	 * 유저별 게시글 조회
	 * @param pageable 조회하려는 페이지
	 * @return 조회된 게시글 반환
	 */
	@GetMapping("/my-board")
	private ResponseEntity<CommonResponse<Page<TradeBoardGetAllResponseDto>>> getByUserId(
		@AuthenticationPrincipal CustomPrincipal currentUser,
		Pageable pageable
	) {
		return CommonResponse.of(SuccessCode.FOUND, tradeBoardService.findByUser(currentUser.getId(), pageable));
	}

	/**
	 * 게시글 수정
	 * @param tradeBoardId 수정하려는 게시글 id
	 * @param requestDto 카테고리, 제목, 내용, 사진, 금액
	 * @return id, 카테고리, 제목, 내용, 사진, 금액, 생성일, 수정일 반환
	 */
	@PutMapping("/{tradeBoardId}")
	private ResponseEntity<CommonResponse<TradeBoardUpdateResponseDto>> updateTradeBoard(
		@AuthenticationPrincipal CustomPrincipal currentUser,
		@PathVariable Long tradeBoardId,
		@Valid @RequestBody TradeBoardUpdateRequestDto requestDto
	) {
		return CommonResponse.of(SuccessCode.UPDATED, tradeBoardService.updateTradeBoard(currentUser.getId(), tradeBoardId, requestDto));
	}

	/**
	 * 게시글 상태 변경
	 * @param tradeBoardId 변경하려는 게시글
	 * @param status 변경하려는 상태값
	 * @return 변환 성공 메시지
	 */
	@PatchMapping("/{tradeBoardId}")
	private ResponseEntity<CommonResponse<Void>> updateTradeBoardStatus(
		@AuthenticationPrincipal CustomPrincipal currentUser,
		@PathVariable Long tradeBoardId,
		@RequestParam TradeBoardStatus status
	) {
		tradeBoardService.updateTradeBoardStatus(currentUser.getId(), tradeBoardId, status);
		return CommonResponse.of(SuccessCode.UPDATED);
	}

	/**
	 * 사진 삭제
	 * @param tradeBoardId 사진 삭제를 진행할 게시글
	 * @param imageId 삭제하려는 사진 아이디
	 * @return 삭제 성공 메시지
	 */
	@DeleteMapping("/{tradeBoardId}/images/{imageId}")
	public ResponseEntity<CommonResponse<Void>> deleteImage(
		@AuthenticationPrincipal CustomPrincipal currentUser,
		@PathVariable Long tradeBoardId,
		@PathVariable Long imageId
	) {
		tradeBoardService.deleteImage(currentUser.getId(), tradeBoardId, imageId);

		return CommonResponse.of(SuccessCode.DELETED);
	}
}
