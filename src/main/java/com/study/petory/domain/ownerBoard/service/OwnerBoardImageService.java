package com.study.petory.domain.ownerBoard.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	private final OwnerBoardImageRepository ownerBoardImageRepository;

	public OwnerBoardImageService(S3Uploader s3Uploader, OwnerBoardImageRepository ownerBoardImageRepository) {
		super(s3Uploader);
		this.ownerBoardImageRepository = ownerBoardImageRepository;
	}

	@Override
	@Transactional // 구현클래스에서 반드시 붙이기
	public void deleteImage(OwnerBoardImage image) {
		deleteImageInternal(image);
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
		ownerBoardImageRepository.save(entity);
	}

	@Override
	protected OwnerBoardImage findImageById(Long imageId) {
		return ownerBoardImageRepository.findById(imageId)
			.orElseThrow(() -> new CustomException(ErrorCode.FILE_NOT_FOUND));
	}

	@Override
	protected String getImageUrl(OwnerBoardImage image) {
		return image.getUrl();
	}

}
