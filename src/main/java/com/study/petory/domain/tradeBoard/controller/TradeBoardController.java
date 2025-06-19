package com.study.petory.domain.tradeBoard.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequiredArgsConstructor
@RequestMapping("/trade-boards")
public class TradeBoardController {

	private final TradeBoardService tradeBoardService;

	/**
	 * 게시글 등록
	 * @param requestDto 카테고리, 제목, 내용, 사진, 금액
	 * @return id, 카테고리, 제목, 내용, 사진, 금액, 생성일 반환
	 */
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	@PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
	public ResponseEntity<CommonResponse<TradeBoardCreateResponseDto>> createTradeBoard(
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
	public ResponseEntity<CommonResponse<Page<TradeBoardGetAllResponseDto>>> getAllTradeBoard(
		@RequestParam(required = false) TradeCategory category,
		@PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
	) {
		return CommonResponse.of(SuccessCode.FOUND, tradeBoardService.findAllTradeBoard(category, pageable));
	}

	/**
	 * 게시글 단건 조회
	 * @param tradeBoardId 조회하려는 게시글 id
	 * @return 해당 게시글 반환
	 */
	@GetMapping("/{tradeBoardId}")
	public ResponseEntity<CommonResponse<TradeBoardGetResponseDto>> getByTradeBoardId(
		@PathVariable Long tradeBoardId
	) {
		return CommonResponse.of(SuccessCode.FOUND, tradeBoardService.findByTradeBoardId(tradeBoardId));
	}

	/**
	 * 유저별 게시글 조회
	 * @param pageable 조회하려는 페이지
	 * @return 조회된 게시글 반환
	 */
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	@GetMapping("/my-board")
	public ResponseEntity<CommonResponse<Page<TradeBoardGetAllResponseDto>>> getByUserId(
		@AuthenticationPrincipal CustomPrincipal currentUser,
		@PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
	) {
		return CommonResponse.of(SuccessCode.FOUND, tradeBoardService.findByUser(currentUser.getId(), pageable));
	}

	/**
	 * 게시글 수정
	 * @param tradeBoardId 수정하려는 게시글 id
	 * @param requestDto 카테고리, 제목, 내용, 사진, 금액
	 * @return id, 카테고리, 제목, 내용, 사진, 금액, 생성일, 수정일 반환
	 */
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	@PutMapping("/{tradeBoardId}")
	public ResponseEntity<CommonResponse<TradeBoardUpdateResponseDto>> updateTradeBoard(
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
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	@PatchMapping("/{tradeBoardId}")
	public ResponseEntity<CommonResponse<Void>> updateTradeBoardStatus(
		@AuthenticationPrincipal CustomPrincipal currentUser,
		@PathVariable Long tradeBoardId,
		@RequestParam TradeBoardStatus status
	) {
		tradeBoardService.updateTradeBoardStatus(currentUser.getId(), tradeBoardId, status);
		return CommonResponse.of(SuccessCode.UPDATED);
	}

	/**
	 * 사진 추가
	 * @param currentUser 로그인한 유저
	 * @param tradeBoardId 게시글
	 * @param images 추가하려는 사진
	 * @return 수정한 게시글
	 */
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	@PostMapping(value = "/{tradeBoardId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<CommonResponse<Void>> addImages(
		@AuthenticationPrincipal CustomPrincipal currentUser,
		@PathVariable Long tradeBoardId,
		@RequestPart List<MultipartFile> images
	) {
		tradeBoardService.addImages(currentUser.getId(), tradeBoardId, images);
		return CommonResponse.of(SuccessCode.CREATED);
	}

	/**
	 * 사진 삭제
	 * @param tradeBoardId 사진 삭제를 진행할 게시글
	 * @param imageId 삭제하려는 사진 아이디
	 * @return 삭제 성공 메시지
	 */
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
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