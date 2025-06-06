package com.study.petory.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
public class AwsS3Config {

	@Value("${spring.cloud.aws.credentials.access-key}")
	public String accessKey;

	@Value("${spring.cloud.aws.credentials.secret-key}")
	public String secretKey;

	@Value("${spring.cloud.aws.region.static}")
	private String region;

	@Bean
	public AmazonS3 amazonS3() {
		// AWS 인증 정보 생성
		BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);

		// S3 클라이언트 생성 및 반환
		return AmazonS3ClientBuilder.standard()
			.withRegion(region)
			.withCredentials(new AWSStaticCredentialsProvider(awsCreds))
			.build();
	}
}
