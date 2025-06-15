package com.study.petory.domain.album.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.study.petory.common.exception.enums.SuccessCode;
import com.study.petory.common.response.CommonResponse;
import com.study.petory.common.security.CustomPrincipal;
import com.study.petory.domain.album.dto.request.AlbumCreateRequestDto;
import com.study.petory.domain.album.dto.request.AlbumUpdateRequestDto;
import com.study.petory.domain.album.dto.request.AlbumVisibilityUpdateRequestDto;
import com.study.petory.domain.album.dto.response.AlbumGetAllResponseDto;
import com.study.petory.domain.album.dto.response.AlbumGetOneResponseDto;
import com.study.petory.domain.album.service.AlbumService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/albums")
@RequiredArgsConstructor
public class AlbumController {

	private final AlbumService albumService;

	/**
	 * 앨범 저장
	 * @param user			앨범을 생성한 유저
	 * @param text			앨범에 작성한 내용
	 * @param images		앨범에 등록하는 이미지
	 * @return	CommonResponse 성공 메세지, data: null
	 */
	@PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
	public ResponseEntity<CommonResponse<Void>> save(
		@AuthenticationPrincipal CustomPrincipal user,
		@RequestPart @Valid AlbumCreateRequestDto text,
		@RequestPart(required = false) List<MultipartFile> images
	) {
		albumService.saveAlbum(user.getId(), text, images);
		return CommonResponse.of(SuccessCode.CREATED);
	}

	/**
	 * 앨범 전체 조회			모든 권한 사용 가능
	 * @param pageable		정렬 기준 및 방식
	 * @return	CommonResponse 성공 메세지, data: 앨범 id, 첫 번째 이미지 url, 생성일
	 */
	@GetMapping("/all")
	public ResponseEntity<CommonResponse<Page<AlbumGetAllResponseDto>>> getAllAlbum(
		@PageableDefault(sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable
	) {
		return CommonResponse.of(SuccessCode.FOUND, albumService.findAllAlbum(pageable));
	}

	/**
	 * 유저의 앨범 전체 조회
	 * @param user			앨범을 생성한 유저
	 * @param pageable		정렬 기준 및 방식
	 * @return	CommonResponse 성공 메세지, data: 앨범 id, 첫 번째 이미지 url, 생성일
	 */
	@GetMapping("/all/user")
	public ResponseEntity<CommonResponse<Page<AlbumGetAllResponseDto>>> getAllUserAlbum(
		@AuthenticationPrincipal CustomPrincipal user,
		@PageableDefault(sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable
	) {
		return CommonResponse.of(SuccessCode.FOUND, albumService.findUserAllAlbum(user.getId(), pageable));
	}

	/**
	 * 앨범 단일 조회
	 * @param albumId		조회하는 앨범 id
	 * @return	CommonResponse 성공 메세지, data: 앨범 id, 이미지 List url, 생성일
	 */
	@GetMapping("/{albumId}")
	public ResponseEntity<CommonResponse<AlbumGetOneResponseDto>> getOneAlbum(
		@AuthenticationPrincipal CustomPrincipal user,
		@PathVariable Long albumId
	) {
		return CommonResponse.of(SuccessCode.FOUND, albumService.findOneAlbum(user.getId(), albumId));
	}

	/**
	 * 앨범 수정
	 * @param user			앨범을 생성한 유저
	 * @param albumId		수정할 앨범 id
	 * @param request		수정할 내용
	 * @return	CommonResponse 성공 메세지, data: null
	 */
	@PutMapping("/{albumId}")
	public ResponseEntity<CommonResponse<Void>> updateAlbum(
		@AuthenticationPrincipal CustomPrincipal user,
		@PathVariable Long albumId,
		@RequestBody AlbumUpdateRequestDto request
	) {
		albumService.updateAlbum(user.getId(), albumId, request);
		return CommonResponse.of(SuccessCode.UPDATED);
	}

	/**
	 * 앨범 공개 여부 변경
	 * @param user			앨범을 생성한 유저
	 * @param albumId		변경할 앨범 id
	 * @param request		변경하는 앨범 공개 여부
	 * @return	CommonResponse 성공 메세지, data: null
	 */
	@PatchMapping("/{albumId}/visibility")
	public ResponseEntity<CommonResponse<Void>> updateVisibility(
		@AuthenticationPrincipal CustomPrincipal user,
		@PathVariable Long albumId,
		@RequestBody AlbumVisibilityUpdateRequestDto request
	) {
		albumService.updateVisibility(user.getId(), albumId, request);
		return CommonResponse.of(SuccessCode.UPDATED);
	}

	/**
	 * 앨범 삭제
	 * @param user			앨범을 생성한 유저
	 * @param albumId		삭제할 앨범 id
	 * @return	CommonResponse 성공 메세지, data: null
	 */
	@DeleteMapping("/{albumId}")
	public ResponseEntity<CommonResponse<Void>> deleteAlbum(
		@AuthenticationPrincipal CustomPrincipal user,
		@PathVariable Long albumId
	) {
		albumService.deleteAlbum(user.getId(), albumId);
		return CommonResponse.of(SuccessCode.DELETED);
	}

	/**
	 * 앨범 사진 추가
	 * @param user			앨범을 생성한 유저
	 * @param albumId		사진을 추가할 앨범 id
	 * @param images		추가할 사진
	 * @return	CommonResponse 성공 메세지, data: null
	 */
	@PostMapping(value = "/{albumId}/images", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
	public ResponseEntity<CommonResponse<Void>> saveNewAlbumImage(
		@AuthenticationPrincipal CustomPrincipal user,
		@PathVariable Long albumId,
		@RequestPart(required = false) List<MultipartFile> images
	) {
		albumService.saveNewAlbumImage(user.getId(), albumId, images);
		return CommonResponse.of(SuccessCode.CREATED);
	}

	/**
	 * 앨범 사진 삭제
	 * @param user			앨범을 생성한 유저
	 * @param albumImageId	삭제할 앨범 이미지 id
	 * @return	CommonResponse 성공 메세지, data: null
	 */
	@DeleteMapping(value = "/images/{albumImageId}")
	public ResponseEntity<CommonResponse<Void>> deleteAlbumImage(
		@AuthenticationPrincipal CustomPrincipal user,
		@PathVariable Long albumImageId
	) {
		albumService.deleteAlbumImage(user.getId(), albumImageId);
		return CommonResponse.of(SuccessCode.DELETED);
	}
}
