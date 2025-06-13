package com.study.petory.domain.album.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.study.petory.common.exception.enums.SuccessCode;
import com.study.petory.common.response.CommonResponse;
import com.study.petory.domain.album.dto.response.AlbumGetAllResponseDto;
import com.study.petory.domain.album.dto.response.AlbumGetOneResponseDto;
import com.study.petory.domain.album.service.AlbumService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AlbumController {

	private final AlbumService albumService;

	@GetMapping("/a")
	public ResponseEntity<CommonResponse<Page<AlbumGetAllResponseDto>>> a(
		@PageableDefault(sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable
	) {
		Long userId = 1L;
		return CommonResponse.of(SuccessCode.FOUND, albumService.findUserAllAlbum(userId, pageable));
	}

	@GetMapping("/b")
	public ResponseEntity<CommonResponse<Page<AlbumGetAllResponseDto>>> b(
		@PageableDefault(sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable
	) {
		Long userId = 1L;
		return CommonResponse.of(SuccessCode.FOUND, albumService.findAllAlbum(pageable));
	}

	@GetMapping("/c")
	public ResponseEntity<CommonResponse<AlbumGetOneResponseDto>> c(
	) {
		Long albumId = 1L;
		return CommonResponse.of(SuccessCode.FOUND, albumService.findOneAlbum(albumId));
	}
}
