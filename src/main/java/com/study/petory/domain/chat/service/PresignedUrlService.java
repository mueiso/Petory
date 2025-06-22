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

		s3Uploader.getExtension(requestDto.getFilename());

		String objectKey = "chat/" + requestDto.getChatRoomId() + "/" + requestDto.getFilename();

		PutObjectRequest objectRequest = PutObjectRequest.builder()
			.bucket(bucket)
			.key(objectKey)
			.contentType(requestDto.getContentType())
			.build();

		PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
			.signatureDuration(Duration.ofMinutes(5))
			.putObjectRequest(objectRequest)
			.build();

		URL uploadUrl = s3Presigner.presignPutObject(presignRequest).url();

		String fileUrl = "https://" + bucket + ".s3." + region + ".amazonaws.com/" + objectKey;

		return new PresignedUrlResponseDto(uploadUrl.toString(), fileUrl);
	}
}
