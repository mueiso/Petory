package com.study.petory.domain.ownerboard.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.study.petory.common.exception.CustomException;
import com.study.petory.common.exception.enums.ErrorCode;
import com.study.petory.common.util.AbstractImageService;
import com.study.petory.common.util.S3Uploader;
import com.study.petory.domain.ownerboard.entity.OwnerBoard;
import com.study.petory.domain.ownerboard.entity.OwnerBoardImage;
import com.study.petory.domain.ownerboard.repository.OwnerBoardImageRepository;

@Service
public class OwnerBoardImageService extends AbstractImageService<OwnerBoardImage> {

	// 도메인 별 repository 및 생성자 구현
	private final OwnerBoardImageRepository ownerBoardImageRepository;

	public OwnerBoardImageService(S3Uploader s3Uploader, OwnerBoardImageRepository ownerBoardImageRepository) {
		super(s3Uploader);
		this.ownerBoardImageRepository = ownerBoardImageRepository;
	}

	// S3에서 이미지 파일 삭제
	@Override
	@Transactional // 구현클래스에서 반드시 붙이기
	public void deleteImage(OwnerBoardImage image) {
		deleteImageInternal(image);
	}

	// S3 버킷 폴더명 설정
	@Override
	protected String getFolderName() {
		return "owner-board";
	}

	// 이미지 URL과 게시글 정보(OwnerBoard)를 기반으로 엔티티 생성
	@Override
	protected OwnerBoardImage createImageEntity(String url, Object context) {
		OwnerBoard ownerBoard = (OwnerBoard)context; // 도메인에 맞게 다운캐스팅
		return new OwnerBoardImage(url, ownerBoard);
	}

	// 이미지 엔티티 DB에 저장
	@Override
	protected void save(OwnerBoardImage entity) {
		ownerBoardImageRepository.save(entity);
	}

	// 이미지 ID로 엔티티 조회
	@Override
	protected OwnerBoardImage findImageById(Long imageId) {
		return ownerBoardImageRepository.findById(imageId)
			.orElseThrow(() -> new CustomException(ErrorCode.FILE_NOT_FOUND));
	}

	// 이미지엔티티에서 Url 추출
	@Override
	protected String getImageUrl(OwnerBoardImage image) {
		return image.getUrl();
	}

}
