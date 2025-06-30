package com.study.petory.domain.pet.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.study.petory.common.exception.enums.SuccessCode;
import com.study.petory.common.response.CommonResponse;
import com.study.petory.common.security.CustomPrincipal;
import com.study.petory.domain.pet.dto.PetCreateRequestDto;
import com.study.petory.domain.pet.dto.PetCreateResponseDto;
import com.study.petory.domain.pet.service.PetService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/pets")
@RequiredArgsConstructor
public class PetController {

	private final PetService petService;

	/**
	 * [펫 생성]
	 * 반려동물을 등록합니다.
	 *
	 * @param currentUser 로그인 유저
	 * @param requestDto 이름, 크기, 종, 성별, 생일
	 * @param images 사진 파일
	 * @return id, 이름, 크기, 종, 성별, 생일, 생성일
	 */
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	@PostMapping
	public ResponseEntity<CommonResponse<PetCreateResponseDto>> createPet(
		@AuthenticationPrincipal CustomPrincipal currentUser,
		@Valid @RequestPart PetCreateRequestDto requestDto,
		@RequestPart(required = false) List<MultipartFile> images) {

		petService.savePet(currentUser.getId(), requestDto, images);

		return CommonResponse.of(SuccessCode.CREATED);
	}


}
