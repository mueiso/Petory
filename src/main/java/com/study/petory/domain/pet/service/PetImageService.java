package com.study.petory.domain.pet.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.study.petory.common.exception.CustomException;
import com.study.petory.common.exception.enums.ErrorCode;
import com.study.petory.common.util.AbstractImageService;
import com.study.petory.common.util.S3Uploader;
import com.study.petory.domain.pet.entity.Pet;
import com.study.petory.domain.pet.entity.PetImage;
import com.study.petory.domain.pet.repository.PetImageRepository;

@Service
public class PetImageService extends AbstractImageService<PetImage> {

	private final PetImageRepository petImageRepository;

	public PetImageService(S3Uploader s3Uploader, PetImageRepository petImageRepository) {
		super(s3Uploader);
		this.petImageRepository = petImageRepository;
	}

	// S3에서 이미지 파일 삭제
	@Override
	@Transactional // 구현클래스에서 반드시 붙이기
	public void deleteImage(PetImage image) {
		deleteImageInternal(image);
	}

	// S3 버킷 폴더명 설정
	@Override
	protected String getFolderName() {
		return "pet";
	}

	// 이미지 URL 과 게시글 정보(OwnerBoard)를 기반으로 엔티티 생성
	@Override
	protected PetImage createImageEntity(String url, Object context) {
		Pet pet = (Pet)context; // 도메인에 맞게 다운캐스팅
		return new PetImage(url, pet);
	}

	// 이미지 엔티티 DB에 저장
	@Override
	protected void save(PetImage entity) {
		petImageRepository.save(entity);
	}

	// 이미지 ID로 엔티티 조회
	@Override
	protected PetImage findImageById(Long imageId) {
		return petImageRepository.findById(imageId)
			.orElseThrow(() -> new CustomException(ErrorCode.FILE_NOT_FOUND));
	}

	// 이미지엔티티에서 Url 추출
	@Override
	protected String getImageUrl(PetImage image) {
		return image.getUrl();
	}
}
