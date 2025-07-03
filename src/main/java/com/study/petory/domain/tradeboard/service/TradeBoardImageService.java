package com.study.petory.domain.tradeboard.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.study.petory.common.exception.CustomException;
import com.study.petory.common.exception.enums.ErrorCode;
import com.study.petory.common.util.AbstractImageService;
import com.study.petory.common.util.S3Uploader;
import com.study.petory.domain.tradeboard.entity.TradeBoard;
import com.study.petory.domain.tradeboard.entity.TradeBoardImage;
import com.study.petory.domain.tradeboard.repository.TradeBoardImageRepository;

@Service
public class TradeBoardImageService extends AbstractImageService<TradeBoardImage> {

	private final TradeBoardImageRepository tradeBoardImageRepository;

	public TradeBoardImageService(S3Uploader s3Uploader, TradeBoardImageRepository tradeBoardImageRepository) {
		super(s3Uploader);
		this.tradeBoardImageRepository = tradeBoardImageRepository;
	}

	@Override
	@Transactional // 구현클래스에서 반드시 붙이기
	public void deleteImage(TradeBoardImage image) {
		deleteImageInternal(image);
	}

	@Override
	protected String getFolderName() {
		return "trade-board";
	}

	@Override
	protected TradeBoardImage createImageEntity(String url, Object context) {
		TradeBoard tradeBoard = (TradeBoard)context; // 도메인에 맞게 다운캐스팅
		return new TradeBoardImage(url, tradeBoard);
	}

	@Override
	protected void save(TradeBoardImage entity) {
		tradeBoardImageRepository.save(entity);
	}

	@Override
	protected TradeBoardImage findImageById(Long imageId) {
		return tradeBoardImageRepository.findById(imageId)
			.orElseThrow(() -> new CustomException(ErrorCode.FILE_NOT_FOUND));
	}

	@Override
	protected String getImageUrl(TradeBoardImage image) {
		return image.getUrl();
	}
}
