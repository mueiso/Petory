package com.study.petory.domain.ownerBoard.service;

import org.springframework.stereotype.Service;

import com.study.petory.common.util.AbstractImageService;
import com.study.petory.common.util.S3Uploader;
import com.study.petory.domain.ownerBoard.entity.OwnerBoard;
import com.study.petory.domain.ownerBoard.entity.OwnerBoardImage;
import com.study.petory.domain.ownerBoard.repository.OwnerBoardImageRepository;
import com.study.petory.exception.CustomException;
import com.study.petory.exception.enums.ErrorCode;

@Service
public class OwnerBoardImageService extends AbstractImageService<OwnerBoardImage> {

	// 도메인 별 repository 및 생성자 구현
	private final OwnerBoardImageRepository repository;

	public OwnerBoardImageService(S3Uploader s3Uploader, OwnerBoardImageRepository repository) {
		super(s3Uploader);
		this.repository = repository;
	}

	@Override
	protected String getFolderName() {
		return "owner-board";
	}

	@Override
	protected OwnerBoardImage createImageEntity(String url, Object context) {
		OwnerBoard ownerBoard = (OwnerBoard)context; // 도메인에 맞게 다운캐스팅
		return new OwnerBoardImage(url, ownerBoard);
	}

	@Override
	protected void save(OwnerBoardImage entity) {
		repository.save(entity);
	}

	@Override
	protected OwnerBoardImage findImageById(Long imageId) {
		return repository.findById(imageId)
			.orElseThrow(() -> new CustomException(ErrorCode.FILE_NOT_FOUND));
	}

	@Override
	protected String getImageUrl(OwnerBoardImage image) {
		return image.getUrl();
	}

	@Override
	protected void deleteImageEntity(OwnerBoardImage image) {
		repository.delete(image);

	}
	//
	// @Transactional
	// public void deleteImage(Long imageId) {
	// 	OwnerBoardImage image = repository.findById(imageId)
	// 		.orElseThrow(() -> new CustomException(ErrorCode.FILE_NOT_FOUND));
	//
	// 	String key = extractKeyFromUrl(image.getUrl());
	//
	// 	s3Uploader.deleteFile(key);
	// 	repository.delete(image);
	// }
	//
	// private String extractKeyFromUrl(String url) {
	// 	String S3_BASE_URL = "https://petory-static-files.s3.ap-northeast-2.amazonaws.com/";
	// 	return url.replace(S3_BASE_URL, "");
	// }
}
