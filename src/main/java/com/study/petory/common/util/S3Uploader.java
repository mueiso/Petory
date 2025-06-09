package com.study.petory.common.util;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.study.petory.exception.CustomException;
import com.study.petory.exception.enums.ErrorCode;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
public class S3Uploader {

	private final S3Client s3Client;

	@Value("${cloud.aws.bucket}")
	private String bucket;

	// 이미지 필터링
	private static final List<String> ALLOWED_EXTENSIONS = List.of("jpg", "jpeg", "png", "gif", "webp");

	// 파일 확장자 추출 메서드
	private String getExtension(String fileName) {
		String ext = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

		if (!ALLOWED_EXTENSIONS.contains(ext)) {

			throw new CustomException(ErrorCode.FILE_INVALID_EXTENSION);
		}

		return ext;
	}

	// S3에 파일 업로드
	public String uploadFile(MultipartFile file, String folder) { //folder로 버킷에서 도메인 구분
		try {
			String ext = getExtension(file.getOriginalFilename());
			String fileName = folder + "/" + UUID.randomUUID() + "." + ext;

			PutObjectRequest putRequest = PutObjectRequest.builder()
				.bucket(bucket)
				.key(fileName)
				.contentType(file.getContentType())
				.build();

			s3Client.putObject(putRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

			return "https://" + bucket + ".s3." + s3Client.serviceClientConfiguration().region().id()
				+ ".amazonaws.com/"
				+ fileName;

		} catch (IOException e) { // file.getInputStream()에서 발생할 수 있는 예외를 처리함
			throw new CustomException(ErrorCode.FILE_UPLOAD_FAILED);
		}
	}

	// S3에 업로드된 파일 삭제
	public void deleteFile(String fileKey) {
		DeleteObjectRequest request = DeleteObjectRequest.builder()
			.bucket(bucket)
			.key(fileKey)
			.build();

		s3Client.deleteObject(request);
	}

}
