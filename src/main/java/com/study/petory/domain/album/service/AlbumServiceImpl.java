package com.study.petory.domain.album.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.study.petory.common.exception.CustomException;
import com.study.petory.common.exception.enums.ErrorCode;
import com.study.petory.domain.album.dto.request.AlbumCreateRequestDto;
import com.study.petory.domain.album.dto.response.AlbumGetAllResponseDto;
import com.study.petory.domain.album.dto.response.AlbumGetOneResponseDto;
import com.study.petory.domain.album.entity.Album;
import com.study.petory.domain.album.entity.AlbumVisibility;
import com.study.petory.domain.album.repository.AlbumRepository;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlbumServiceImpl implements AlbumService {

	private final AlbumRepository albumRepository;

	private final AlbumImageServiceImpl albumImageService;

	// refactor 예정
	private final UserRepository userRepository;

	// 엘범 저장
	@Override
	public void saveAlbum(Long userId, AlbumCreateRequestDto requestDto, List<MultipartFile> images) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		AlbumVisibility albumVisibility = AlbumVisibility.PUBLIC;

		if (requestDto.getAlbumVisibility() == null) {
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

	// 유저의 앨범 전체 조회
	@Override
	public Page<AlbumGetAllResponseDto> findUserAllAlbum(Long userId, Pageable pageable) {
		Page<Album> albumPage = findPageAlbum(userId, pageable);
		return albumPage.map(AlbumGetAllResponseDto::from);
	}

	// 단일 앨범 조회
	@Override
	public AlbumGetOneResponseDto findOneAlbum(Long albumId) {
		Album album = albumRepository.findOneAlbumByUser(albumId)
			.orElseThrow(() -> new CustomException(ErrorCode.ALBUM_NOT_FOUND));
		return AlbumGetOneResponseDto.from(album);
	}

	// 앨범 전체 조회
	@Override
	public Page<AlbumGetAllResponseDto> findAllAlbum(Pageable pageable) {
		Page<Album> albumPage = findPageAlbum(null, pageable);
		return albumPage.map(AlbumGetAllResponseDto::from);
	}

	@Override
	public void updateAlbum() {

	}

	@Override
	public void deleteAlbum() {

	}

	@Override
	public void findDeletedAlbum() {

	}

	@Override
	public void restoreAlbum() {

	}

	@Override
	public Page<Album> findPageAlbum(Long userId, Pageable pageable) {
		return albumRepository.findAllAlbum(userId, pageable);
	}
}
