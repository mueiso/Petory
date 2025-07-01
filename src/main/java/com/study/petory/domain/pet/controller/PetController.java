package com.study.petory.domain.pet.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.study.petory.common.exception.enums.SuccessCode;
import com.study.petory.common.response.CommonResponse;
import com.study.petory.common.security.CustomPrincipal;
import com.study.petory.domain.pet.dto.PetCreateRequestDto;
import com.study.petory.domain.pet.dto.PetGetAllResponseDto;
import com.study.petory.domain.pet.dto.PetResponseDto;
import com.study.petory.domain.pet.dto.PetUpdateRequestDto;
import com.study.petory.domain.pet.dto.PetUpdateResponseDto;
import com.study.petory.domain.pet.service.PetService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/pets")
@RequiredArgsConstructor
public class PetController {

	private final PetService petService;

	/**
	 * [반려동물 생성]
	 * 반려동물을 등록합니다.
	 *
	 * @param currentUser 로그인 유저
	 * @param requestDto 이름, 크기, 종, 성별, 생일
	 * @param images 사진 파일
	 * @return 생성 성공 메시지
	 */
	@PostMapping(consumes = {"multipart/form-data"})
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	public ResponseEntity<CommonResponse<PetResponseDto>> createPet(
		@AuthenticationPrincipal CustomPrincipal currentUser,
		@Valid @RequestPart("requestDto") PetCreateRequestDto requestDto,
		@RequestPart(required = false) List<MultipartFile> images) {

		petService.savePet(currentUser.getId(), requestDto, images);

		return CommonResponse.of(SuccessCode.CREATED);
	}

	/**
	 * [내 반려동물 목록 조회]
	 *
	 * @param currentUser 로그인 유저
	 * @param pageable 최신순 목록 조회
	 * @return 해당 유저가 등록한 반려동물 전체 목록 페이징 처리되어 반환
	 */
	@GetMapping
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	public ResponseEntity<CommonResponse<Page<PetGetAllResponseDto>>> getMyPets(
		@AuthenticationPrincipal CustomPrincipal currentUser,
		@PageableDefault(page = 0, size = 5, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

		Page<PetGetAllResponseDto> response = petService.findAllMyPets(currentUser.getId(), pageable);

		return CommonResponse.of(SuccessCode.FOUND, response);
	}

	/**
	 * [반려동물 단건 조회]
	 *
	 * @param petId 등록된 펫 id
	 * @return id, 이름, 크기, 종, 성별, 생일, 사진, 생성일
	 */
	@GetMapping("/{petId}")
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	public ResponseEntity<CommonResponse<PetResponseDto>> getPet(
		@AuthenticationPrincipal CustomPrincipal currentUser,
		@PathVariable Long petId) {

		return CommonResponse.of(SuccessCode.FOUND, petService.findPet(currentUser.getId(), petId));
	}

	/**
	 * [반려동물 정보 수정]
	 *
	 * @param currentUser 로그인 유저
	 * @param petId 정보 수정 대상 반려동물
	 * @param requestDto 이름, 성별, 생일
	 * @param images 동물 프로필 사진 (선택)
	 * @return 수정된 내용
	 */
	@PutMapping(value = "/{petId}", consumes = {"multipart/form-data"})
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	public ResponseEntity<CommonResponse<PetUpdateResponseDto>> updatePet(
		@AuthenticationPrincipal CustomPrincipal currentUser,
		@PathVariable Long petId,
		@Valid @RequestPart PetUpdateRequestDto requestDto,
		@RequestPart(required = false) List<MultipartFile> images) {

		PetUpdateResponseDto response = petService.updatePet(currentUser.getId(), petId, requestDto, images);

		return CommonResponse.of(SuccessCode.UPDATED, response);
	}

	/**
	 * [반려동물 프로필 사진 삭제]
	 *
	 * @param currentUser 로그인 유저
	 * @param petImageId 삭제 대상 이미지
	 * @return 삭제 성공 메시지
	 */
	@DeleteMapping("/images/{petImageId}")
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	public ResponseEntity<CommonResponse<Void>> deletePetImage(
		@AuthenticationPrincipal CustomPrincipal currentUser,
		@PathVariable Long petImageId) {

		petService.deletePetImage(currentUser.getId(), petImageId);

		return CommonResponse.of(SuccessCode.DELETED);
	}

	/**
	 * [반려동물 삭제]
	 * 펫 정보를 soft delete 합니다.
	 *
	 * @param currentUser 로그인 유저
	 * @param petId 정보 삭제 대상 반려동물
	 * @return 삭제 성공 메시지
	 */
	@DeleteMapping("/{petId}")
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	public ResponseEntity<CommonResponse<Void>> deletePet(
		@AuthenticationPrincipal CustomPrincipal currentUser,
		@PathVariable Long petId) {

		petService.deletePet(currentUser.getId(), petId);

		return CommonResponse.of(SuccessCode.DELETED);
	}

	/**
	 * [반려동물 복구]
	 *
	 * @param currentUser 로그인 유저
	 * @param petId 복구 대상 반려동물
	 * @return 복구 성공 메시지
	 */
	@PatchMapping("/{petId}/restore")
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	public ResponseEntity<CommonResponse<Void>> restorePet(
		@AuthenticationPrincipal CustomPrincipal currentUser,
		@PathVariable Long petId) {

		petService.restorePet(currentUser.getId(), petId);

		return CommonResponse.of(SuccessCode.RESTORED);
	}
}
