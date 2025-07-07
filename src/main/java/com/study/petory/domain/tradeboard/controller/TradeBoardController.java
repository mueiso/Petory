package com.study.petory.domain.tradeboard.controller;

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
import com.study.petory.domain.tradeboard.dto.request.TradeBoardCreateRequestDto;
import com.study.petory.domain.tradeboard.dto.request.TradeBoardUpdateRequestDto;
import com.study.petory.domain.tradeboard.dto.response.TradeBoardCreateResponseDto;
import com.study.petory.domain.tradeboard.dto.response.TradeBoardGetAllResponseDto;
import com.study.petory.domain.tradeboard.dto.response.TradeBoardGetResponseDto;
import com.study.petory.domain.tradeboard.dto.response.TradeBoardUpdateResponseDto;
import com.study.petory.domain.tradeboard.entity.TradeBoardStatus;
import com.study.petory.domain.tradeboard.entity.TradeCategory;
import com.study.petory.domain.tradeboard.service.TradeBoardService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/trade-boards")
public class TradeBoardController {

	private final TradeBoardService tradeBoardService;

	/**
	 * 게시글 생성
	 * @param currentUser 로그인된 사용자 정보
	 * @param requestDto 카테고리, 제목, 내용, 가격
	 * @param images 거래 물품 사진
	 * @return 게시글Id, 카테고리, 제목, 내용, 가격, 이미지url, 생성일
	 */
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	@PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
	public ResponseEntity<CommonResponse<TradeBoardCreateResponseDto>> createTradeBoard(
		@AuthenticationPrincipal CustomPrincipal currentUser,
		@Valid @RequestPart TradeBoardCreateRequestDto requestDto,
		@RequestPart(required = false) List<MultipartFile> images
	) {
		return CommonResponse.of(SuccessCode.CREATED,
			tradeBoardService.saveTradeBoard(currentUser.getId(), requestDto, images));
	}

	/**
	 * 게시글 전체조회
	 * @param category 조회하려는 카테고리
	 * @param pageable 페이징 정보
	 * @return 페이징된 게시글 리스트(게시글Id, 작성자 닉네임, 제목, 내용, 가격, 이미지url)
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
	 * @param tradeBoardId 게시글Id
	 * @return 게시글Id, 판매자Id, 제목, 내용, 가격, 이미지url, 생성일, 수정일
	 */
	@GetMapping("/{tradeBoardId}")
	public ResponseEntity<CommonResponse<TradeBoardGetResponseDto>> getByTradeBoardId(
		@PathVariable Long tradeBoardId
	) {
		return CommonResponse.of(SuccessCode.FOUND, tradeBoardService.findByTradeBoardId(tradeBoardId));
	}

	/**
	 * 유저별 게시글 조회
	 * @param currentUser 로그인된 사용자 정보
	 * @param pageable 페이징 정보
	 * @return 페이징된 게시글 리스트(게시글Id, 작성자 닉네임, 제목, 내용, 가격, 이미지url)
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
	 * @param currentUser 로그인된 사용자 정보
	 * @param tradeBoardId 게시글Id
	 * @param requestDto 수정하려는 정보(카테고리, 제목, 내용, 가격)
	 * @return 게시글Id, 카테고리, 제목, 내용, 가격, 이미지url, 생성일, 수정일
	 */
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	@PutMapping("/{tradeBoardId}")
	public ResponseEntity<CommonResponse<TradeBoardUpdateResponseDto>> updateTradeBoard(
		@AuthenticationPrincipal CustomPrincipal currentUser,
		@PathVariable Long tradeBoardId,
		@Valid @RequestBody TradeBoardUpdateRequestDto requestDto
	) {
		return CommonResponse.of(SuccessCode.UPDATED,
			tradeBoardService.updateTradeBoard(currentUser.getId(), tradeBoardId, requestDto));
	}

	/**
	 * 게시글 상태 변경
	 * @param currentUser 로그인된 사용자 정보
	 * @param tradeBoardId 게시글Id
	 * @param status 게시글 상태
	 * @return 200 응답
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
	 * 게시글 사진 추가
	 * @param currentUser 로그인된 사용자 정보
	 * @param tradeBoardId 게시글Id
	 * @param images 추가하려는 사진
	 * @return 200 응답
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
	 * 게시글 사진 삭제
	 * @param currentUser 로그인된 사용자 정보
	 * @param tradeBoardId 게시글Id
	 * @param imageId 이미지Id
	 * @return 200응답
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