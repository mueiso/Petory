package com.study.petory.common.util;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ServiceClientConfiguration;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@ExtendWith(MockitoExtension.class)
public class S3UploaderTest {

	@Mock
	private S3Client s3Client;

	@Mock
	private MultipartFile mockFile;

	@InjectMocks
	private S3Uploader s3Uploader;

	private String bucket = "petory-static-files";

	@BeforeEach
	void setBucketField() {
		ReflectionTestUtils.setField(s3Uploader, "bucket", bucket);
	}

	@Test
	void 파일_업로드에_성공한다 () throws IOException {
		// given
		String folder = "domain-name";
		String fileName = "image.jpg";
		String regionId = "ap-northeast-2";

		// mockFile 관련 mocking
		given(mockFile.getOriginalFilename()).willReturn(fileName);
		given(mockFile.getContentType()).willReturn("image/jpeg");
		given(mockFile.getInputStream()).willReturn(new ByteArrayInputStream("test".getBytes(StandardCharsets.UTF_8)));
		given(mockFile.getSize()).willReturn(4L);

		S3ServiceClientConfiguration mockConfig = mock(S3ServiceClientConfiguration.class);
		Region mockRegion = mock(Region.class);

		given(s3Client.serviceClientConfiguration()).willReturn(mockConfig);
		given(mockConfig.region()).willReturn(mockRegion);
		given(mockRegion.id()).willReturn(regionId);

		// when
		String result = s3Uploader.uploadFile(mockFile, folder);

		// then
		assertTrue(result.contains("https://"));
		assertTrue(result.contains(regionId));
		assertTrue(result.contains(folder));
		verify(s3Client).putObject(any(PutObjectRequest.class),any(RequestBody.class));
	}

	@Test
	void 파일_삭제에_성공한다() {
		// given
		String fileKey = "domain-name/1234";

		// when
		s3Uploader.deleteFile(fileKey);

		// then
		verify(s3Client, times(1)).deleteObject(any(DeleteObjectRequest.class));
	}

}
