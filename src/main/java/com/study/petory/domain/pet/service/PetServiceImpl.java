package com.study.petory.domain.pet.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.study.petory.domain.pet.dto.PetCreateRequestDto;
import com.study.petory.domain.pet.dto.PetCreateResponseDto;
import com.study.petory.domain.pet.entity.Pet;
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
	public PetCreateResponseDto savePet(Long userId, PetCreateRequestDto requestDto, List<MultipartFile> images) {

		User user = userService.findUserById(userId);

		Pet pet = Pet.builder()
			.name(requestDto.getName())
			.size(requestDto.getSize())  // PetSize enum
			.species(requestDto.getSpecies())
			.gender(requestDto.getGender())
			.birthday(requestDto.getBirthday())
			.build();

		petRepository.save(pet);

		List<String> urls = new ArrayList<>();

		if (images != null && !images.isEmpty()) {
			urls = petImageService.uploadAndSaveAll(images, pet);
		}

		return PetCreateResponseDto.of(pet, urls);
	}
}
