package com.study.petory.domain.album.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.study.petory.common.exception.CustomException;
import com.study.petory.common.exception.enums.ErrorCode;
import com.study.petory.domain.album.dto.request.AlbumCreateRequestDto;
import com.study.petory.domain.album.dto.request.AlbumUpdateRequestDto;
import com.study.petory.domain.album.dto.request.AlbumVisibilityUpdateRequestDto;
import com.study.petory.domain.album.dto.response.AlbumGetAllResponseDto;
import com.study.petory.domain.album.dto.response.AlbumGetOneResponseDto;
import com.study.petory.domain.album.entity.Album;
import com.study.petory.domain.album.entity.AlbumImage;
import com.study.petory.domain.album.entity.AlbumVisibility;
import com.study.petory.domain.album.repository.AlbumRepository;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlbumServiceImpl implements AlbumService {

	private final AlbumRepository albumRepository;

	private final AlbumImageServiceImpl albumImageService;

	private final UserService userService;

	// 엘범 저장
	@Override
	@Transactional
	public void saveAlbum(Long userId, AlbumCreateRequestDto requestDto, List<MultipartFile> images) {
		User user = userService.getUserById(userId);

		AlbumVisibility albumVisibility = AlbumVisibility.PUBLIC;

		if (requestDto.getAlbumVisibility().equals(AlbumVisibility.PRIVATE)) {
			albumVisibility = AlbumVisibility.PRIVATE;
		}

		Album album = Album.builder()
			.user(user)
			.content(requestDto.getContent())
			.albumVisibility(albumVisibility)
			.build();

		albumRepository.save(album);
		albumImageService.uploadAndSaveAll(images, album);
	}

	// 앨범 전체 조회
	@Override
	public Page<AlbumGetAllResponseDto> findAllAlbum(Pageable pageable) {
		Page<Album> albumPage = findAlbumByPage(null, pageable);
		return albumPage.map(AlbumGetAllResponseDto::from);
	}

	// 유저의 앨범 전체 조회
	@Override
	public Page<AlbumGetAllResponseDto> findUserAllAlbum(Long userId, Pageable pageable) {
		Page<Album> albumPage = findAlbumByPage(userId, pageable);
		return albumPage.map(AlbumGetAllResponseDto::from);
	}

	// 앨범 단일 조회
	@Override
	public AlbumGetOneResponseDto findOneAlbum(Long userId, Long albumId) {
		boolean showOnlyPublic = true;
		if (userId != null) {
			showOnlyPublic = false;
		}
		return AlbumGetOneResponseDto.from(findAlbumByAlbumId(showOnlyPublic, albumId));
	}

	// 앨범 수정
	@Override
	@Transactional
	public void updateAlbum(Long userId, Long albumId, AlbumUpdateRequestDto request) {
		Album album = findAlbumByAlbumId(false, albumId);
		validateAuthor(userId, album);
		album.updateAlbum(
			request.getContent()
		);
	}

	// 앨범 공개 여부 변경
	@Override
	@Transactional
	public void updateVisibility(Long userId, Long albumId, AlbumVisibilityUpdateRequestDto request) {
		Album album = findAlbumByAlbumId(false, albumId);
		validateAuthor(userId, album);
		album.updateVisibility(request.getAlbumVisibility());
	}

	// 앨범 삭제
	@Override
	@Transactional
	public void deleteAlbum(Long userId, Long albumId) {
		Album album = findAlbumByAlbumId(false, albumId);
		validateAuthor(userId, album);

		List<AlbumImage> albumImageList = album.getAlbumImageList();

		for (AlbumImage image : new ArrayList<>(albumImageList)) {
			albumImageService.deleteImage(image);
			album.getAlbumImageList().remove(image);
		}
		albumRepository.deleteById(albumId);
	}

	// 앨범 사진 추가
	@Override
	@Transactional
	public void saveNewAlbumImage(Long userId, Long albumId, List<MultipartFile> images) {
		Album album = findAlbumByAlbumId(false, albumId);
		validateAuthor(userId, album);
		albumImageService.uploadAndSaveAll(images, album);
	}

	// 앨범 사진 삭제
	@Override
	@Transactional
	public void deleteAlbumImage(Long userId, Long imageId) {
		AlbumImage albumImage = albumImageService.findImageById(imageId);
		Album album = findAlbumByAlbumId(false, albumImage.getAlbum().getId());

		albumImageService.deleteImageInternal(albumImage);
		album.getAlbumImageList().remove(albumImage);
	}

	// 앨범 페이징과 첫 이미지 조회
	@Override
	public Page<Album> findAlbumByPage(Long userId, Pageable pageable) {
		return albumRepository.findAllAlbum(userId, pageable);
	}

	// 앨범과 앨범의 이미지 조회
	@Override
	public Album findAlbumByAlbumId(boolean showOnlyPublic, Long albumId) {
		return albumRepository.findOneAlbumByUser(showOnlyPublic, albumId)
			.orElseThrow(() -> new CustomException(ErrorCode.ALBUM_NOT_FOUND));
	}

	// 작성자의 앨범인지 검증
	@Override
	public void validateAuthor(Long userId, Album album) {
		if (!album.isEqualUser(userId)) {
			throw new CustomException(ErrorCode.ONLY_AUTHOR_CAN_EDIT);
		}
	}
}
