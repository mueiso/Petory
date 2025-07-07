package com.study.petory.domain.ownerboard.service;

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

@ExtendWith(MockitoExtension.class)
public class OwnerBoardImageServiceTest {

	@Mock
	private S3Uploader s3Uploader;

	@Mock
	private OwnerBoardImageRepository ownerBoardImageRepository;

	@InjectMocks
	private OwnerBoardImageService ownerBoardImageService;

	@Test
	void 이미지_업로드_및_엔티티_반환_성공() {
		// given
		List<MultipartFile> files = List.of(
			mock(MultipartFile.class),
			mock(MultipartFile.class)
		);
		OwnerBoard ownerBoard = mock(OwnerBoard.class);

		given(s3Uploader.uploadFile(any(MultipartFile.class), eq("owner-board")))
			.willReturn("https://image1.jpg")
			.willReturn("https://image2.jpg");

		// when
		List<OwnerBoardImage> result = ownerBoardImageService.uploadAndReturnEntities(files, ownerBoard);

		// then
		assertEquals(2, result.size());
		verify(s3Uploader, times(2)).uploadFile(any(MultipartFile.class), eq("owner-board"));
		verify(ownerBoardImageRepository, times(2)).save(any(OwnerBoardImage.class));
	}

	@Test
	void 이미지_업로드_및_URL_반환_성공() {
		// given
		List<MultipartFile> files = List.of(mock(MultipartFile.class));
		OwnerBoard ownerBoard = mock(OwnerBoard.class);

		given(s3Uploader.uploadFile(any(MultipartFile.class), eq("owner-board")))
			.willReturn("https://image.jpg");

		// when
		List<String> result = ownerBoardImageService.uploadAndSaveAll(files, ownerBoard);

		// then
		assertEquals(1, result.size());
		assertEquals("https://image.jpg", result.get(0));
		verify(s3Uploader).uploadFile(any(MultipartFile.class), eq("owner-board"));
		verify(ownerBoardImageRepository).save(any(OwnerBoardImage.class));
	}

	@Test
	void 이미지_삭제_성공() {
		// given
		OwnerBoardImage image = mock(OwnerBoardImage.class);
		given(image.getUrl()).willReturn("https://test-bucket.s3.us-east-1.amazonaws.com/owner-board/image.jpg");

		// @Value 필드 모킹
		ReflectionTestUtils.setField(ownerBoardImageService, "bucket", "test-bucket");
		ReflectionTestUtils.setField(ownerBoardImageService, "region", "us-east-1");

		// when
		ownerBoardImageService.deleteImage(image);

		// then
		verify(s3Uploader).deleteFile("owner-board/image.jpg");
	}

	@Test
	void 이미지_ID로_조회_성공() {
		// given
		Long imageId = 1L;
		OwnerBoardImage image = mock(OwnerBoardImage.class);
		given(ownerBoardImageRepository.findById(imageId)).willReturn(Optional.of(image));

		// when
		OwnerBoardImage result = ownerBoardImageService.findImageById(imageId);

		// then
		assertEquals(image, result);
		verify(ownerBoardImageRepository).findById(imageId);
	}

	@Test
	void createImageEntity_정상_동작_확인() {
		// given
		String url = "https://image.jpg";
		OwnerBoard ownerBoard = mock(OwnerBoard.class);

		// when
		OwnerBoardImage result = ownerBoardImageService.createImageEntity(url, ownerBoard);

		// then
		assertNotNull(result);
		assertEquals(url, result.getUrl());
		assertEquals(ownerBoard, result.getOwnerBoard());
	}

	@Test
	void getFolderName_반환값_확인() {
		// when
		String folderName = ownerBoardImageService.getFolderName();

		// then
		assertEquals("owner-board", folderName);
	}

	@Test
	void getImageUrl_반환값_확인() {
		// given
		OwnerBoardImage image = mock(OwnerBoardImage.class);
		given(image.getUrl()).willReturn("https://image.jpg");

		// when
		String url = ownerBoardImageService.getImageUrl(image);

		// then
		assertEquals("https://image.jpg", url);
	}
}
