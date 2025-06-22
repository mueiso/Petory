package com.study.petory.domain.chat.service;

import java.net.URL;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.study.petory.common.util.S3Uploader;
import com.study.petory.domain.chat.dto.request.PresignedUrlRequestDto;
import com.study.petory.domain.chat.dto.response.PresignedUrlResponseDto;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
@RequiredArgsConstructor
public class PresignedUrlService {

	private final S3Presigner s3Presigner;
	private final S3Uploader s3Uploader;

	@Value("${spring.cloud.aws.region.static}")
	private String region;

	@Value("${cloud.aws.bucket}")
	private String bucket;

	public PresignedUrlResponseDto createPresignedUrl(PresignedUrlRequestDto requestDto) {

		// 파일 확장자 추출
		s3Uploader.getExtension(requestDto.getFilename());

		// 업로드될 S3 객체의 Key 경로 생성
		String objectKey = "chat/" + requestDto.getChatRoomId() + "/" + requestDto.getFilename();

		// 업로드 객체 생성
		PutObjectRequest objectRequest = PutObjectRequest.builder()
			.bucket(bucket)
			.key(objectKey)
			.contentType(requestDto.getContentType())
			.build();

		// Presigned Url 생성을 위한 요청 객체 생성
		PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
			.signatureDuration(Duration.ofMinutes(5)) //시간은 우선 5분으로 지정
			.putObjectRequest(objectRequest)
			.build();

		// Presigned URL 생성
		URL uploadUrl = s3Presigner.presignPutObject(presignRequest).url();

		// 업로드 완료 후 접근 가능한 정적 파일 URL 생성
		String fileUrl = "https://" + bucket + ".s3." + region + ".amazonaws.com/" + objectKey;

		return new PresignedUrlResponseDto(uploadUrl.toString(), fileUrl);
	}
}
