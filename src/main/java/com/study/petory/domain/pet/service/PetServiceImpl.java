package com.study.petory.domain.pet.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.study.petory.domain.pet.dto.PetCreateRequestDto;
import com.study.petory.domain.pet.dto.PetCreateResponseDto;

@Service
public class PetServiceImpl implements PetService{

	// Pet 생성
	@Override
	@Transactional
	public PetCreateResponseDto savePet(Long userId, PetCreateRequestDto requestDto) {


	}
}
