package com.study.petory.domain.pet.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.study.petory.common.exception.CustomException;
import com.study.petory.common.exception.enums.ErrorCode;
import com.study.petory.domain.pet.dto.PetCreateRequestDto;
import com.study.petory.domain.pet.dto.PetUpdateRequestDto;
import com.study.petory.domain.pet.entity.Pet;
import com.study.petory.domain.pet.entity.PetSize;
import com.study.petory.domain.pet.repository.PetRepository;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.service.UserService;

@ExtendWith(MockitoExtension.class)
class PetServiceImplTest {

	@Mock
	private UserService userService;

	@Mock
	private PetRepository petRepository;

	@InjectMocks
	private PetServiceImpl petService;

	@Test
	void savePet_등록_성공() {

		// given
		Long userId = 1L;
		PetCreateRequestDto requestDto = new PetCreateRequestDto("쿠키", PetSize.SMALL, "푸들", "남", "2022-01-01");

		User mockUser = mock(User.class);
		given(userService.findUserById(userId)).willReturn(mockUser);

		ArgumentCaptor<Pet> petCaptor = ArgumentCaptor.forClass(Pet.class);

		// when
		petService.savePet(userId, requestDto, null);

		// then
		verify(petRepository).save(petCaptor.capture());
		verify(mockUser).addPet(any(Pet.class)); // 양방향 연관관계 설정 확인
		assertThat(petCaptor.getValue().getName()).isEqualTo("쿠키");
	}

	@Test
	void findPet_조회_실패_petNotFound() {

		// given
		Long userId = 1L;
		Long petId = 100L;

		given(petRepository.findPetById(petId)).willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> petService.findPet(userId, petId))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.PET_NOT_FOUND.getMessage());
	}
}
