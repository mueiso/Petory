package com.study.petory.domain.ownerboard.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.study.petory.domain.ownerboard.dto.request.OwnerBoardCreateRequestDto;
import com.study.petory.domain.ownerboard.dto.request.OwnerBoardUpdateRequestDto;
import com.study.petory.domain.ownerboard.dto.response.OwnerBoardCreateResponseDto;
import com.study.petory.domain.ownerboard.dto.response.OwnerBoardGetAllResponseDto;
import com.study.petory.domain.ownerboard.dto.response.OwnerBoardGetResponseDto;
import com.study.petory.domain.ownerboard.dto.response.OwnerBoardUpdateResponseDto;
import com.study.petory.domain.ownerboard.entity.OwnerBoard;

public interface OwnerBoardService {
	OwnerBoard findOwnerBoardById(Long boardId);

	OwnerBoardCreateResponseDto saveOwnerBoard(Long userId, OwnerBoardCreateRequestDto dto, List<MultipartFile> images);

	Page<OwnerBoardGetAllResponseDto> findAllOwnerBoards(String title, Pageable pageable);

	OwnerBoardGetResponseDto findOwnerBoard(Long boardId);

	OwnerBoardUpdateResponseDto updateOwnerBoard(Long userId, Long boardId, OwnerBoardUpdateRequestDto dto);

	void deleteOwnerBoard(Long userId, Long boardId);

	void restoreOwnerBoard(Long userId, Long boardId);

	void addImages(Long userId, Long boardId, List<MultipartFile> images);

	void deleteImage(Long userId, Long boardId, Long imageId);

}
