package com.study.petory.common.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

public abstract class AbstractImageService<T> {

	@Value("${cloud.aws.bucket}")
	private String bucket;

	@Value("${spring.cloud.aws.region.static}")
	private String region;

	protected final S3Uploader s3Uploader;

	public AbstractImageService(S3Uploader s3Uploader) {
		this.s3Uploader = s3Uploader;
	}

	// 파일 업로드 및 저장
	public List<String> uploadAndSaveAll(List<MultipartFile> files, Object context) {
		List<String> urls = new ArrayList<>();

		if (files == null || files.isEmpty()) {
			return urls; //파일이 없을 때 빈 리스트 반환
		}

		for (MultipartFile file : files) {
			String url = s3Uploader.uploadFile(file, getFolderName());
			T entity = createImageEntity(url, context);
			save(entity);
			urls.add(url);
		}

		return urls;
	}

	// 파일 삭제
	@Transactional
	public void deleteImage(Long imageId) {
		T image = findImageById(imageId);
		String key = extractKeyFromUrl(getImageUrl(image));
		s3Uploader.deleteFile(key);
		deleteImageEntity(image);
	}

	// 공통 유틸
	protected String extractKeyFromUrl(String url) {
		String baseUrl = "https://" + bucket + ".s3." + region + ".amazonaws.com/";
		return url.replace(baseUrl, "");
	}

	// 도메인별로 구현 필요(업로드 및 저장 용도)
	protected abstract String getFolderName(); // 도메인별 폴더명

	protected abstract T createImageEntity(String url, Object context); // 도메인별 엔티티 생성

	protected abstract void save(T entity); // 도메인별 DB 저장로직

	// 도메인별 구현 필요(삭제 용도)
	protected abstract T findImageById(Long imageId);

	protected abstract String getImageUrl(T image);

	protected abstract void deleteImageEntity(T image);
}
