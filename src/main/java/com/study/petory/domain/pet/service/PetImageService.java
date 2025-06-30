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

	@Override
	@Transactional
	public void deleteImage(PetImage image) {
		deleteImageInternal(image);
	}

	@Override
	protected String getFolderName() {
		return "pet";
	}

	@Override
	protected PetImage createImageEntity(String url, Object context) {
		Pet pet = (Pet) context;
		return new PetImage(url, pet);
	}

	@Override
	protected void save(PetImage entity) {
		petImageRepository.save(entity);
	}

	@Override
	protected PetImage findImageById(Long imageId) {
		return petImageRepository.findById(imageId)
			.orElseThrow(() -> new CustomException(ErrorCode.FILE_NOT_FOUND));
	}

	@Override
	protected String getImageUrl(PetImage image) {
		return image.getUrl();
	}
}
