package com.study.petory.domain.pet.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.study.petory.common.exception.CustomException;
import com.study.petory.common.exception.enums.ErrorCode;
import com.study.petory.domain.pet.dto.PetCreateRequestDto;
import com.study.petory.domain.pet.dto.PetGetAllResponseDto;
import com.study.petory.domain.pet.dto.PetResponseDto;
import com.study.petory.domain.pet.dto.PetUpdateRequestDto;
import com.study.petory.domain.pet.dto.PetUpdateResponseDto;
import com.study.petory.domain.pet.entity.Pet;
import com.study.petory.domain.pet.entity.PetImage;
import com.study.petory.domain.pet.repository.PetRepository;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PetServiceImpl implements PetService {

	private final UserService userService;
	private final PetRepository petRepository;
	private final PetImageService petImageService;

	// 반려동물 등록
	@Override
	@Transactional
	public void savePet(Long userId, PetCreateRequestDto requestDto, List<MultipartFile> images) {

		User user = userService.findUserById(userId);

		Pet pet = Pet.builder()
			.name(requestDto.getName())
			.size(requestDto.getSize())  // PetSize enum
			.species(requestDto.getSpecies())
			.gender(requestDto.getGender())
			.birthday(requestDto.getBirthday())
			.build();

		user.addPet(pet);

		petRepository.save(pet);

		if (images != null && !images.isEmpty()) {
			petImageService.uploadAndSaveAll(images, pet);
		}
	}

	// 반려동물 목록 전체 조회
	@Override
	@Transactional(readOnly = true)
	public Page<PetGetAllResponseDto> findAllMyPets(Long userId, Pageable pageable) {

		User user = userService.findUserById(userId);

		Page<Pet> pets = petRepository.findAllByUserAndDeletedAtIsNull(user, pageable);

		return pets.map(PetGetAllResponseDto::of);
	}

	// 반려동물 단건 조회
	@Override
	@Transactional(readOnly = true)
	public PetResponseDto findPet(Long userId, Long petId) {

		Pet pet = findPetById(petId);

		// 본인 소유 아닐 경우 예외 처리
		if (!pet.isPetOwner(userId)) {
			throw new CustomException(ErrorCode.FORBIDDEN);
		}

		if (!pet.isDeletedAtNull()) {
			throw new CustomException(ErrorCode.PET_NOT_FOUND);
		}

		List<String> urls = pet.getImages().stream()
			.map(PetImage::getUrl)
			.toList();

		return PetResponseDto.of(pet, urls);
	}

	// 반려동물 정보 수정
	@Override
	@Transactional
	public PetUpdateResponseDto updatePet(Long userId, Long petId, PetUpdateRequestDto requestDto,
		List<MultipartFile> images) {

		Pet pet = findPetById(petId);

		if (!pet.isPetOwner(userId)) {
			throw new CustomException(ErrorCode.FORBIDDEN);
		}

		// 기존 이미지 제거
		if (pet.getImages() != null && !pet.getImages().isEmpty()) {
			petImageService.deleteAll(pet.getImages());
			pet.getImages().clear();
		}

		// 새로운 이미지 등록
		List<String> imageUrls = new ArrayList<>();

		if (images != null && !images.isEmpty()) {
			imageUrls = petImageService.uploadAndSaveAll(images, pet);
		}

		// 정보 업데이트
		pet.updatePetInfo(requestDto.getName(), requestDto.getGender(), requestDto.getBirthday());

		return PetUpdateResponseDto.of(pet, imageUrls);
	}

	// 반려동물 프로필 사진 삭제
	@Override
	@Transactional
	public void deletePetImage(Long userId, Long petImageId) {

		PetImage image = petImageService.findImageById(petImageId);

		Pet pet = image.getPet();

		if (!pet.isPetOwner(userId)) {
			throw new CustomException(ErrorCode.FORBIDDEN);
		}

		// 이미지 삭제 (S3 삭제 + DB 삭제)
		petImageService.deleteImage(image);

		// 연관관계 제거 (양방향 매핑 유지 위해)
		pet.getImages().remove(image);
	}

	// 반려동물 삭제
	@Override
	@Transactional
	public void deletePet(Long userId, Long petId) {

		Pet pet = findPetById(petId);

		if (!pet.isPetOwner(userId)) {
			throw new CustomException(ErrorCode.ONLY_AUTHOR_CAN_DELETE);
		}

		// 이미지 모두 hard delete(S3, DB)
		List<PetImage> images = pet.getImages();

		for (PetImage image : new ArrayList<>(images)) {
			petImageService.deleteImage(image);  // S3 이미지 삭제
			pet.getImages().remove(image);  // DB 이미지 삭제
		}

		pet.deactivateEntity();
	}

	// 반려동물 복구
	@Override
	@Transactional
	public void restorePet(Long userId, Long petId) {

		Pet pet = findPetById(petId);

		if (pet.isDeletedAtNull()) {
			throw new CustomException(ErrorCode.PET_NOT_DELETED);
		}

		if (!pet.isPetOwner(userId)) {
			throw new CustomException(ErrorCode.FORBIDDEN);
		}

		pet.restoreEntity();
	}

	@Override
	public Pet findPetById(Long petId) {
		return petRepository.findPetById(petId)
			.orElseThrow(() -> new CustomException(ErrorCode.PET_NOT_FOUND));
	}
}
