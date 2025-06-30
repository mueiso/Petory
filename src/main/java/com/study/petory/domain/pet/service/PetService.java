package com.study.petory.domain.pet.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.study.petory.domain.pet.dto.PetCreateRequestDto;
import com.study.petory.domain.pet.dto.PetResponseDto;
import com.study.petory.domain.pet.dto.PetUpdateRequestDto;
import com.study.petory.domain.pet.dto.PetUpdateResponseDto;

public interface PetService {

	void savePet(Long userId, PetCreateRequestDto requestDto, List<MultipartFile> images);

	PetResponseDto findPet(Long petId);

	PetUpdateResponseDto updatePet(Long userId, Long petId, PetUpdateRequestDto requestDto, List<MultipartFile> images);
}
