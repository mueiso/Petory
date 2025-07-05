package com.study.petory.domain.pet.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import com.study.petory.common.util.S3Uploader;
import com.study.petory.domain.ownerboard.entity.OwnerBoard;
import com.study.petory.domain.ownerboard.entity.OwnerBoardImage;
import com.study.petory.domain.ownerboard.repository.OwnerBoardImageRepository;
import com.study.petory.domain.ownerboard.service.OwnerBoardImageService;
import com.study.petory.domain.pet.entity.Pet;
import com.study.petory.domain.pet.entity.PetImage;
import com.study.petory.domain.pet.repository.PetImageRepository;

@ExtendWith(MockitoExtension.class)
public class PetImageServiceTest {

	@Mock
	private S3Uploader s3Uploader;

	@Mock
	private PetImageRepository petImageRepository;

	@InjectMocks
	private PetImageService petImageService;

	@Test
	void 이미지_업로드_및_엔티티_반환_성공() {
		// given
		List<MultipartFile> files = List.of(
			mock(MultipartFile.class),
			mock(MultipartFile.class)
		);
		Pet pet = mock(Pet.class);

		given(s3Uploader.uploadFile(any(MultipartFile.class), eq("pet")))
			.willReturn("https://image1.jpg")
			.willReturn("https://image2.jpg");

		// when
		List<PetImage> result = petImageService.uploadAndReturnEntities(files, pet);

		// then
		assertEquals(2, result.size());
		verify(s3Uploader, times(2)).uploadFile(any(MultipartFile.class), eq("pet"));
		verify(petImageRepository, times(2)).save(any(PetImage.class));
	}

	@Test
	void 이미지_업로드_및_URL_반환_성공() {
		// given
		List<MultipartFile> files = List.of(mock(MultipartFile.class));
		Pet pet = mock(Pet.class);

		given(s3Uploader.uploadFile(any(MultipartFile.class), eq("pet")))
			.willReturn("https://image.jpg");

		// when
		List<String> result = petImageService.uploadAndSaveAll(files, pet);

		// then
		assertEquals(1, result.size());
		assertEquals("https://image.jpg", result.get(0));
		verify(s3Uploader).uploadFile(any(MultipartFile.class), eq("pet"));
		verify(petImageRepository).save(any(PetImage.class));
	}

	@Test
	void 이미지_삭제_성공() {
		// given
		PetImage image = mock(PetImage.class);
		given(image.getUrl()).willReturn("https://test-bucket.s3.us-east-1.amazonaws.com/pet/image.jpg");

		// @Value 필드 모킹
		ReflectionTestUtils.setField(petImageService, "bucket", "test-bucket");
		ReflectionTestUtils.setField(petImageService, "region", "us-east-1");

		// when
		petImageService.deleteImage(image);

		// then
		verify(s3Uploader).deleteFile("pet/image.jpg");
	}

	@Test
	void 이미지_ID로_조회_성공() {
		// given
		Long imageId = 1L;
		PetImage image = mock(PetImage.class);
		given(petImageRepository.findById(imageId)).willReturn(Optional.of(image));

		// when
		PetImage result = petImageService.findImageById(imageId);

		// then
		assertEquals(image, result);
		verify(petImageRepository).findById(imageId);
	}

	@Test
	void createImageEntity_정상_동작_확인() {
		// given
		String url = "https://image.jpg";
		Pet pet = mock(Pet.class);

		// when
		PetImage result = petImageService.createImageEntity(url, pet);

		// then
		assertNotNull(result);
		assertEquals(url, result.getUrl());
		assertEquals(pet, result.getPet());
	}

	@Test
	void getFolderName_반환값_확인() {
		// when
		String folderName = petImageService.getFolderName();

		// then
		assertEquals("pet", folderName);
	}

	@Test
	void getImageUrl_반환값_확인() {
		// given
		PetImage image = mock(PetImage.class);
		given(image.getUrl()).willReturn("https://image.jpg");

		// when
		String url = petImageService.getImageUrl(image);

		// then
		assertEquals("https://image.jpg", url);
	}
}
