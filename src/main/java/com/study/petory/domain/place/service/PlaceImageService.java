package com.study.petory.domain.place.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.study.petory.common.exception.CustomException;
import com.study.petory.common.exception.enums.ErrorCode;
import com.study.petory.common.util.AbstractImageService;
import com.study.petory.common.util.S3Uploader;
import com.study.petory.domain.place.entity.Place;
import com.study.petory.domain.place.entity.PlaceImage;
import com.study.petory.domain.place.repository.PlaceImageRepository;

@Service
public class PlaceImageService extends AbstractImageService<PlaceImage> {

	// 도메인 별 repository 및 생성자 구현
	private final PlaceImageRepository placeImageRepository;

	public PlaceImageService(S3Uploader s3Uploader,
		PlaceImageRepository placeImageRepository) {
		super(s3Uploader);
		this.placeImageRepository = placeImageRepository;
	}

	// S3 버킷 폴더명 설정
	@Override
	protected String getFolderName() {
		return "place";
	}

	// 이미지 URL과 게시글 정보(OwnerBoard)를 기반으로 엔티티 생성
	@Override
	protected PlaceImage createImageEntity(String url, Object context) {
		Place place = (Place)context;
		return new PlaceImage(url, place);
	}

	// 이미지 엔티티 DB에 저장
	@Override
	protected void save(PlaceImage entity) {
		placeImageRepository.save(entity);
	}

	// S3에서 이미지 파일 삭제
	@Override
	@Transactional
	public void deleteImage(PlaceImage image) {
		deleteImageInternal(image);
	}

	// 이미지 ID로 엔티티 조회
	@Override
	protected PlaceImage findImageById(Long imageId) {
		return placeImageRepository.findById(imageId)
			.orElseThrow(() -> new CustomException(ErrorCode.FILE_NOT_FOUND));
	}

	// 이미지 엔티티에서 Url 추출
	@Override
	protected String getImageUrl(PlaceImage image) {
		return image.getUrl();
	}

	protected List<PlaceImage> findImagesByPlaceId(Long placeId) {
		return placeImageRepository.findAllByPlace_Id(placeId);
	}
}
