package com.study.petory.domain.pet.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import com.study.petory.common.exception.CustomException;
import com.study.petory.common.exception.enums.ErrorCode;
import com.study.petory.common.util.S3Uploader;
import com.study.petory.domain.pet.entity.Pet;
import com.study.petory.domain.pet.entity.PetImage;
import com.study.petory.domain.pet.repository.PetImageRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class PetImageServiceTest {

	@Mock
	private S3Uploader s3Uploader;

	@Mock
	private PetImageRepository petImageRepository;

	@InjectMocks
	private PetImageService petImageService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void deleteImage_정상_삭제_호출() {

		// given
		Pet pet = new Pet();  // 기본 생성자 또는 목 객체
		PetImage image = new PetImage("https://s3.test/pet.jpg", pet);

		// doNothing().when(s3Uploader).deleteFile(anyString()); // 내부 호출 확인은 생략

		// when
		petImageService.deleteImage(image);

		// then
		verify(s3Uploader).deleteFile(eq("https://s3.test/pet.jpg"));
	}

	@Test
	void findImageById_정상적으로_조회() {

		// given
		Long id = 1L;
		Pet pet = new Pet();
		PetImage image = new PetImage("https://s3.test/image.jpg", pet);

		when(petImageRepository.findById(id)).thenReturn(Optional.of(image));

		// when
		PetImage found = petImageService.findImageById(id);

		// then
		assertEquals("https://s3.test/image.jpg", found.getUrl());
	}

	@Test
	void findImageById_이미지_없을_경우_예외발생() {

		// given
		Long id = 999L;
		when(petImageRepository.findById(id)).thenReturn(Optional.empty());

		// when & then
		CustomException ex = assertThrows(CustomException.class, () -> petImageService.findImageById(id));
		assertEquals(ErrorCode.FILE_NOT_FOUND, ex.getErrorCode());
	}

	@Test
	void createImageEntity_정상생성() {

		// given
		Pet pet = new Pet();
		String url = "https://s3.test/created.jpg";

		// when
		PetImage image = petImageService.createImageEntity(url, pet);

		// then
		assertEquals(url, image.getUrl());
		assertEquals(pet, image.getPet());
	}

	@Test
	void getFolderName_pet반환() {

		assertEquals("pet", petImageService.getFolderName());
	}
}
