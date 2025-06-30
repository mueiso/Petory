package com.study.petory.domain.pet.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.study.petory.common.exception.CustomException;
import com.study.petory.common.exception.enums.ErrorCode;
import com.study.petory.domain.pet.dto.PetCreateRequestDto;
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

	// Pet 생성
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
			.user(user)
			.build();

		petRepository.save(pet);

		List<String> urls = new ArrayList<>();

		if (images != null && !images.isEmpty()) {
			urls = petImageService.uploadAndSaveAll(images, pet);
		}
	}

	// 펫 단건 조회
	@Override
	public PetResponseDto findPet(Long petId) {

		Pet pet = petRepository.findById(petId)
			.orElseThrow(() -> new CustomException(ErrorCode.PET_NOT_FOUND));

		List<String> urls = pet.getImages().stream()
			.map(PetImage::getUrl)
			.toList();

		return PetResponseDto.of(pet, urls);
	}

	// 반려동물 정보 수정
	@Override
	@Transactional
	public PetUpdateResponseDto updatePet(Long userId, Long petId, PetUpdateRequestDto requestDto, List<MultipartFile> images) {

		Pet pet = petRepository.findById(petId)
			.orElseThrow(() -> new CustomException(ErrorCode.PET_NOT_FOUND));

		// 본인 소유 아닐 경우 예외 처리
		if (!pet.getUser().getId().equals(userId)) {
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
}
