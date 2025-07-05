package com.study.petory.domain.ownerboard.service;

import static com.study.petory.domain.user.entity.UserStatus.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
		List<OwnerBoardImage> result = ownerBoardImageService.uploadAndReturnEntities(files,ownerBoard);

		// then
		assertEquals(2, result.size());
		verify(s3Uploader, times(2)).uploadFile(any(MultipartFile.class), eq("owner-board"));
		verify(ownerBoardImageRepository, times(2)).save(any(OwnerBoardImage.class));
	}

}
