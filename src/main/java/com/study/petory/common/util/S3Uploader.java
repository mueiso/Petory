package com.study.petory.common.util;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
public class S3Uploader {

	private final S3Client s3Client;

	@Value("${cloud.aws.bucket}")
	private String bucket;

	private String getExtension(String fileName) {
		return fileName.substring(fileName.lastIndexOf(".") + 1);
	}

	public String uploadFile(MultipartFile file, String folder) throws IOException {
		String ext = getExtension(file.getOriginalFilename());
		String fileName = folder + "/" + UUID.randomUUID() + "." + ext;

		PutObjectRequest putRequest = PutObjectRequest.builder()
			.bucket(bucket)
			.key(fileName)
			.contentType(file.getContentType())
			// .acl(ObjectCannedACL.PUBLIC_READ)
			.build();

		s3Client.putObject(putRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

		return "https://" + bucket + ".s3." + s3Client.serviceClientConfiguration().region().id() + ".amazonaws.com/"
			+ fileName;
	}

}
