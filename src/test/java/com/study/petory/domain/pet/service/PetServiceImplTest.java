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

		/* [given]
		 * 등록 요청 DTO 생성
		 */
		Long userId = 1L;
		PetCreateRequestDto requestDto = new PetCreateRequestDto("쿠키", PetSize.SMALL, "푸들", "남", "2022-01-01");

		/*
		 * 유저 mock 객체 생성
		 * 유저 서비스 호출 시 mockUser 반환
		 */
		User mockUser = mock(User.class);
		given(userService.findUserById(userId)).willReturn(mockUser);

		/*
		 * 저장될 Pet 객체를 캡처하기 위한 도구
		 * ArgumentCaptor<Pet>의 역할: PetRepository.save() 메서드가 호출될 때 실제로 전달된 Pet 객체가 뭔지 가로채서 저장해줌
		 */
		ArgumentCaptor<Pet> petCaptor = ArgumentCaptor.forClass(Pet.class);

		/* [when]
		 * 이미지 없이 등록 시도
		 */
		petService.savePet(userId, requestDto, null);

		/* [then]
		 * petRepository.save 호출 확인 및 캡처
		 * 양방향 연관관계 설정 메서드 호출 확인
		 * 저장된 Pet 의 name 필드 검증
		 */
		verify(petRepository).save(petCaptor.capture());
		verify(mockUser).addPet(any(Pet.class));
		assertThat(petCaptor.getValue().getName()).isEqualTo("쿠키");
	}

	@Test
	void findPet_조회_실패_petNotFound() {

		// given
		Long userId = 1L;
		Long petId = 100L;

		// 없는 ID 로 조회 시 빈 Optional 반환
		given(petRepository.findPetById(petId)).willReturn(Optional.empty());

		/* [when & then]
		 * 예외 발생 예상
		 * CustomException 인지 확인
		 * 에러 메시지 일치 여부 확인
		 */
		assertThatThrownBy(() -> petService.findPet(userId, petId))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.PET_NOT_FOUND.getMessage());
	}

	@Test
	void updatePet_수정_실패_소유자아님() {

		// [given]
		Long userId = 1L;
		Long petId = 1L;
		PetUpdateRequestDto requestDto = new PetUpdateRequestDto("수정이름", "여", "2020-01-01");

		/*
		 * 수정 대상 반려동물 mock 객체
		 * 해당 ID 로 조회 시 mockPet 반환
		 * 유저가 소유자 아님으로 설정
		 */
		Pet mockPet = mock(Pet.class);
		given(petRepository.findPetById(petId)).willReturn(Optional.of(mockPet));
		given(mockPet.isPetOwner(userId)).willReturn(false);

		/* [when & then]
		 * 접근 권한 없음 예외 확인
		 */
		assertThatThrownBy(() -> petService.updatePet(userId, petId, requestDto, null))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.FORBIDDEN.getMessage());
	}

	@Test
	void deletePet_삭제_성공() {

		/* [given]
		 * userId 설정
		 * petId 설정
		 * 삭제할 반려동물 mock
		 * DB 에서 찾았다고 가정
		 * 사용자 소유 확인
		 * 이미지 없다고 가정 (단순화)
		 */
		Long userId = 1L;
		Long petId = 1L;
		Pet mockPet = mock(Pet.class);
		given(petRepository.findPetById(petId)).willReturn(Optional.of(mockPet));
		given(mockPet.isPetOwner(userId)).willReturn(true);
		given(mockPet.getImages()).willReturn(List.of());

		// [when]
		petService.deletePet(userId, petId);

		/* [then]
		 * soft delete 메서드 호출되었는지 확인
		 */
		verify(mockPet).deactivateEntity();
	}

	@Test
	void restorePet_복구_실패_notDeleted() {

		/* [given]
		 * userId 설정
		 * petId 설정
		 * 복구할 반려동물 mock
		 * DB 에서 찾았다고 가정
		 * 삭제된 상태가 아님
		 */
		Long userId = 1L;
		Long petId = 1L;
		Pet mockPet = mock(Pet.class);
		given(petRepository.findPetById(petId)).willReturn(Optional.of(mockPet));
		given(mockPet.isDeletedAtNull()).willReturn(true);

		/* [when & then]
		 * 복구 시도
		 * 이미 삭제되지 않은 상태에서 예외 발생 확인
		 */
		assertThatThrownBy(() -> petService.restorePet(userId, petId))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.PET_NOT_DELETED.getMessage());
	}
}
