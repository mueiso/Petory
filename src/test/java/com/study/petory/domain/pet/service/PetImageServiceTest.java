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
		// @Mock, @InjectMocks 애노테이션 초기화
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void deleteImage_정상_삭제_호출() {

		/* [given]
		 * 단순 Pet 객체 생성 (실제 DB 와 연동하지 않음)
		 * 삭제할 PetImage 객체를 mock 으로 객체 생성
		 */
		Pet pet = new Pet();
		PetImage image = new PetImage("https://s3.test/pet.jpg", pet);

		/* [when]
		 * 삭제 로직 수행
		 */
		petImageService.deleteImage(image);

		/* [then]
		 * S3Uploader 의 deleteFile 이 해당 url 로 호출됐는지 검증
		 */
		verify(s3Uploader).deleteFile(eq("https://s3.test/pet.jpg"));
	}

	@Test
	void findImageById_정상적으로_조회() {

		// [given]
		Long id = 1L;
		Pet pet = new Pet();
		PetImage image = new PetImage("https://s3.test/image.jpg", pet);

		// petImageRepository.findById(id) 호출 시 image 를 반환하도록 설정
		when(petImageRepository.findById(id)).thenReturn(Optional.of(image));

		/* [when]
		 * 테스트 대상 메서드 호출
		 */
		PetImage found = petImageService.findImageById(id);

		/* [then]
		 * url 일치하는지 검증
		 */
		assertEquals("https://s3.test/image.jpg", found.getUrl());
	}

	@Test
	void findImageById_이미지_없을_경우_예외발생() {

		/* [given]
		 * 해당 ID 로 조회 시 Optional.empty() 반환하도록 설정
		 */
		Long id = 999L;
		when(petImageRepository.findById(id)).thenReturn(Optional.empty());

		/* [when & then]
		 * 예외 발생 테스트
		 * 발생한 예외의 ErrorCode 가 FILE_NOT_FOUND 인지 검증
		 */
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
