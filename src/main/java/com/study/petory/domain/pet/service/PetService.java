package com.study.petory.domain.pet.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.study.petory.domain.pet.dto.PetCreateRequestDto;
import com.study.petory.domain.pet.dto.PetCreateResponseDto;

public interface PetService {

	public PetCreateResponseDto savePet(Long userId, PetCreateRequestDto requestDto, List<MultipartFile> images);
}
