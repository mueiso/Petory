package com.study.petory.domain.place.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
import com.study.petory.domain.place.entity.Place;
import com.study.petory.domain.place.entity.PlaceImage;
import com.study.petory.domain.place.repository.PlaceImageRepository;

@ExtendWith(MockitoExtension.class)
public class PlaceImageServiceTest {

	@Mock
	private S3Uploader s3Uploader;

	@Mock
	private PlaceImageRepository placeImageRepository;

	@InjectMocks
	private PlaceImageService placeImageService;

	@Test
	void 이미지_업로드_및_엔티티_반환_성공() {
		// given
		List<MultipartFile> files = List.of(
			mock(MultipartFile.class),
			mock(MultipartFile.class)
		);
		Place place = mock(Place.class);

		given(s3Uploader.uploadFile(any(MultipartFile.class), eq("place")))
			.willReturn("https://image1.jpg")
			.willReturn("https://image2.jpg");

		// when
		List<PlaceImage> result = placeImageService.uploadAndReturnEntities(files, place);

		// then
		assertEquals(2, result.size());
		verify(s3Uploader, times(2)).uploadFile(any(MultipartFile.class), eq("place"));
		verify(placeImageRepository, times(2)).save(any(PlaceImage.class));
	}

	@Test
	void 이미지_업로드_및_URL_반환_성공() {
		// given
		List<MultipartFile> files = List.of(mock(MultipartFile.class));
		Place place = mock(Place.class);

		given(s3Uploader.uploadFile(any(MultipartFile.class), eq("place")))
			.willReturn("https://image.jpg");

		// when
		List<String> result = placeImageService.uploadAndSaveAll(files, place);

		// then
		assertEquals(1, result.size());
		assertEquals("https://image.jpg", result.get(0));
		verify(s3Uploader).uploadFile(any(MultipartFile.class), eq("place"));
		verify(placeImageRepository).save(any(PlaceImage.class));
	}

	@Test
	void 이미지_삭제_성공() {
		// given
		PlaceImage image = mock(PlaceImage.class);
		given(image.getUrl()).willReturn("https://test-bucket.s3.us-east-1.amazonaws.com/place/image.jpg");

		// @Value 필드 모킹
		ReflectionTestUtils.setField(placeImageService, "bucket", "test-bucket");
		ReflectionTestUtils.setField(placeImageService, "region", "us-east-1");

		// when
		placeImageService.deleteImage(image);

		// then
		verify(s3Uploader).deleteFile("place/image.jpg");
	}

	@Test
	void 이미지_ID로_조회_성공() {
		// given
		Long imageId = 1L;
		PlaceImage image = mock(PlaceImage.class);
		given(placeImageRepository.findById(imageId)).willReturn(Optional.of(image));

		// when
		PlaceImage result = placeImageService.findImageById(imageId);

		// then
		assertEquals(image, result);
		verify(placeImageRepository).findById(imageId);
	}

	@Test
	void createImageEntity_정상_동작_확인() {
		// given
		String url = "https://image.jpg";
		Place place = mock(Place.class);

		// when
		PlaceImage result = placeImageService.createImageEntity(url, place);

		// then
		assertNotNull(result);
		assertEquals(url, result.getUrl());
		assertEquals(place, result.getPlace());
	}

	@Test
	void getFolderName_반환값_확인() {
		// when
		String folderName = placeImageService.getFolderName();

		// then
		assertEquals("place", folderName);
	}

	@Test
	void getImageUrl_반환값_확인() {
		// given
		PlaceImage image = mock(PlaceImage.class);
		given(image.getUrl()).willReturn("https://image.jpg");

		// when
		String url = placeImageService.getImageUrl(image);

		// then
		assertEquals("https://image.jpg", url);
	}
}
