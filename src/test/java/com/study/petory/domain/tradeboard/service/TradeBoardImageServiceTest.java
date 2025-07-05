package com.study.petory.domain.tradeboard.service;

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
import com.study.petory.domain.tradeboard.entity.TradeBoard;
import com.study.petory.domain.tradeboard.entity.TradeBoardImage;
import com.study.petory.domain.tradeboard.repository.TradeBoardImageRepository;

@ExtendWith(MockitoExtension.class)
public class TradeBoardImageServiceTest {
	@Mock
	private S3Uploader s3Uploader;

	@Mock
	private TradeBoardImageRepository tradeBoardImageRepository;

	@InjectMocks
	private TradeBoardImageService tradeBoardImageService;

	@Test
	void 이미지_업로드_및_엔티티_반환_성공() {
		// given
		List<MultipartFile> files = List.of(
			mock(MultipartFile.class),
			mock(MultipartFile.class)
		);
		TradeBoard tradeBoard = mock(TradeBoard.class);

		given(s3Uploader.uploadFile(any(MultipartFile.class), eq("trade-board")))
			.willReturn("https://image1.jpg")
			.willReturn("https://image2.jpg");

		// when
		List<TradeBoardImage> result = tradeBoardImageService.uploadAndReturnEntities(files, tradeBoard);

		// then
		assertEquals(2, result.size());
		verify(s3Uploader, times(2)).uploadFile(any(MultipartFile.class), eq("trade-board"));
		verify(tradeBoardImageRepository, times(2)).save(any(TradeBoardImage.class));
	}

	@Test
	void 이미지_업로드_및_URL_반환_성공() {
		// given
		List<MultipartFile> files = List.of(mock(MultipartFile.class));
		TradeBoard tradeBoard = mock(TradeBoard.class);

		given(s3Uploader.uploadFile(any(MultipartFile.class), eq("trade-board")))
			.willReturn("https://image.jpg");

		// when
		List<String> result = tradeBoardImageService.uploadAndSaveAll(files, tradeBoard);

		// then
		assertEquals(1, result.size());
		assertEquals("https://image.jpg", result.get(0));
		verify(s3Uploader).uploadFile(any(MultipartFile.class), eq("trade-board"));
		verify(tradeBoardImageRepository).save(any(TradeBoardImage.class));
	}

	@Test
	void 이미지_삭제_성공() {
		// given
		TradeBoardImage image = mock(TradeBoardImage.class);
		given(image.getUrl()).willReturn("https://test-bucket.s3.us-east-1.amazonaws.com/trade-board/image.jpg");

		// @Value 필드 모킹
		ReflectionTestUtils.setField(tradeBoardImageService, "bucket", "test-bucket");
		ReflectionTestUtils.setField(tradeBoardImageService, "region", "us-east-1");

		// when
		tradeBoardImageService.deleteImage(image);

		// then
		verify(s3Uploader).deleteFile("trade-board/image.jpg");
	}

	@Test
	void 이미지_ID로_조회_성공() {
		// given
		Long imageId = 1L;
		TradeBoardImage image = mock(TradeBoardImage.class);
		given(tradeBoardImageRepository.findById(imageId)).willReturn(Optional.of(image));

		// when
		TradeBoardImage result = tradeBoardImageService.findImageById(imageId);

		// then
		assertEquals(image, result);
		verify(tradeBoardImageRepository).findById(imageId);
	}

	@Test
	void createImageEntity_정상_동작_확인() {
		// given
		String url = "https://image.jpg";
		TradeBoard tradeBoard = mock(TradeBoard.class);

		// when
		TradeBoardImage result = tradeBoardImageService.createImageEntity(url, tradeBoard);

		// then
		assertNotNull(result);
		assertEquals(url, result.getUrl());
		assertEquals(tradeBoard, result.getTradeBoard());
	}

	@Test
	void getFolderName_반환값_확인() {
		// when
		String folderName = tradeBoardImageService.getFolderName();

		// then
		assertEquals("trade-board", folderName);
	}

	@Test
	void getImageUrl_반환값_확인() {
		// given
		TradeBoardImage image = mock(TradeBoardImage.class);
		given(image.getUrl()).willReturn("https://image.jpg");

		// when
		String url = tradeBoardImageService.getImageUrl(image);

		// then
		assertEquals("https://image.jpg", url);
	}
}
