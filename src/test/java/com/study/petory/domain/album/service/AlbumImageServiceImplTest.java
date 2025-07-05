package com.study.petory.domain.album.service;

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
import com.study.petory.domain.album.entity.Album;
import com.study.petory.domain.album.entity.AlbumImage;
import com.study.petory.domain.album.repository.AlbumImageRepository;

@ExtendWith(MockitoExtension.class)

public class AlbumImageServiceImplTest {

	@Mock
	private S3Uploader s3Uploader;

	@Mock
	private AlbumImageRepository albumImageRepository;

	@InjectMocks
	private AlbumImageServiceImpl albumImageService;

	@Test
	void 이미지_업로드_및_엔티티_반환_성공() {
		// given
		List<MultipartFile> files = List.of(
			mock(MultipartFile.class),
			mock(MultipartFile.class)
		);
		Album album = mock(Album.class);

		given(s3Uploader.uploadFile(any(MultipartFile.class), eq("album-image")))
			.willReturn("https://image1.jpg")
			.willReturn("https://image2.jpg");

		// when
		List<AlbumImage> result = albumImageService.uploadAndReturnEntities(files, album);

		// then
		assertEquals(2, result.size());
		verify(s3Uploader, times(2)).uploadFile(any(MultipartFile.class), eq("album-image"));
		verify(albumImageRepository, times(2)).save(any(AlbumImage.class));
	}

	@Test
	void 이미지_업로드_및_URL_반환_성공() {
		// given
		List<MultipartFile> files = List.of(mock(MultipartFile.class));
		Album album = mock(Album.class);

		given(s3Uploader.uploadFile(any(MultipartFile.class), eq("album-image")))
			.willReturn("https://image.jpg");

		// when
		List<String> result = albumImageService.uploadAndSaveAll(files, album);

		// then
		assertEquals(1, result.size());
		assertEquals("https://image.jpg", result.get(0));
		verify(s3Uploader).uploadFile(any(MultipartFile.class), eq("album-image"));
		verify(albumImageRepository).save(any(AlbumImage.class));
	}

	@Test
	void 이미지_삭제_성공() {
		// given
		AlbumImage image = mock(AlbumImage.class);
		given(image.getUrl()).willReturn("https://test-bucket.s3.us-east-1.amazonaws.com/album-image/image.jpg");

		// @Value 필드 모킹
		ReflectionTestUtils.setField(albumImageService, "bucket", "test-bucket");
		ReflectionTestUtils.setField(albumImageService, "region", "us-east-1");

		// when
		albumImageService.deleteImage(image);

		// then
		verify(s3Uploader).deleteFile("album-image/image.jpg");
	}

	@Test
	void 이미지_ID로_조회_성공() {
		// given
		Long imageId = 1L;
		AlbumImage image = mock(AlbumImage.class);
		given(albumImageRepository.findById(imageId)).willReturn(Optional.of(image));

		// when
		AlbumImage result = albumImageService.findImageById(imageId);

		// then
		assertEquals(image, result);
		verify(albumImageRepository).findById(imageId);
	}

	@Test
	void createImageEntity_정상_동작_확인() {
		// given
		String url = "https://image.jpg";
		Album album = mock(Album.class);

		// when
		AlbumImage result = albumImageService.createImageEntity(url, album);

		// then
		assertNotNull(result);
		assertEquals(url, result.getUrl());
		assertEquals(album, result.getAlbum());
	}

	@Test
	void getFolderName_반환값_확인() {
		// when
		String folderName = albumImageService.getFolderName();

		// then
		assertEquals("album-image", folderName);
	}

	@Test
	void getImageUrl_반환값_확인() {
		// given
		AlbumImage image = mock(AlbumImage.class);
		given(image.getUrl()).willReturn("https://image.jpg");

		// when
		String url = albumImageService.getImageUrl(image);

		// then
		assertEquals("https://image.jpg", url);
	}

}
