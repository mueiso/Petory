package com.study.petory.domain.album.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.study.petory.common.exception.CustomException;
import com.study.petory.common.exception.enums.ErrorCode;
import com.study.petory.common.util.AbstractImageService;
import com.study.petory.common.util.S3Uploader;
import com.study.petory.domain.album.entity.Album;
import com.study.petory.domain.album.entity.AlbumImage;
import com.study.petory.domain.album.repository.AlbumImageRepository;

@Service
public class AlbumImageServiceImpl extends AbstractImageService<AlbumImage> {

	private final AlbumImageRepository albumImageRepository;

	public AlbumImageServiceImpl(S3Uploader s3Uploader, AlbumImageRepository albumImageRepository) {
		super(s3Uploader);
		this.albumImageRepository = albumImageRepository;
	}

	@Override
	@Transactional
	public void deleteImage(AlbumImage image) {
		deleteImageInternal(image);
	}

	@Override
	protected String getFolderName() {
		return "album-image";
	}

	@Override
	protected AlbumImage createImageEntity(String url, Object context) {
		return new AlbumImage(url, (Album)context);
	}

	@Override
	@Transactional
	protected void save(AlbumImage entity) {
		albumImageRepository.save(entity);
	}

	@Override
	protected AlbumImage findImageById(Long imageId) {
		return albumImageRepository.findById(imageId)
			.orElseThrow(() -> new CustomException(ErrorCode.ALBUM_IMAGE_NOT_FOUND));
	}

	@Override
	protected String getImageUrl(AlbumImage image) {
		return image.getUrl();
	}
}
