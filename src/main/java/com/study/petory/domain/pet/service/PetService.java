package com.study.petory.domain.pet.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.study.petory.domain.pet.dto.PetCreateRequestDto;
import com.study.petory.domain.pet.dto.PetResponseDto;

public interface PetService {

	PetResponseDto savePet(Long userId, PetCreateRequestDto requestDto, List<MultipartFile> images);

	PetResponseDto findPet(Long petId);
}
