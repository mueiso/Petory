package com.study.petory.domain.pet.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.study.petory.domain.pet.dto.PetCreateRequestDto;
import com.study.petory.domain.pet.dto.PetResponseDto;
import com.study.petory.domain.pet.dto.PetUpdateRequestDto;
import com.study.petory.domain.pet.dto.PetUpdateResponseDto;
import com.study.petory.domain.pet.entity.Pet;

public interface PetService {

	void savePet(Long userId, PetCreateRequestDto requestDto, List<MultipartFile> images);

	PetResponseDto findPet(Long userId, Long petId);

	PetUpdateResponseDto updatePet(Long userId, Long petId, PetUpdateRequestDto requestDto, List<MultipartFile> images);

	void deletePet(Long userId, Long petId);

	Pet findPetById(Long petId);
}
