package com.study.petory.domain.ownerBoard.service;

import org.springframework.stereotype.Service;

import com.study.petory.common.util.AbstractImageService;
import com.study.petory.common.util.S3Uploader;
import com.study.petory.domain.ownerBoard.entity.OwnerBoard;
import com.study.petory.domain.ownerBoard.entity.OwnerBoardImage;
import com.study.petory.domain.ownerBoard.repository.OwnerBoardImageRepository;

@Service
public class OwnerBoardImageService extends AbstractImageService<OwnerBoardImage> {

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
		OwnerBoard ownerBoard = (OwnerBoard) context;
		return new OwnerBoardImage(url, ownerBoard);
	}

	@Override
	protected void save(OwnerBoardImage entity) {
		repository.save(entity);
	}
}
