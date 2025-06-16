package com.study.petory.domain.album.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.study.petory.domain.album.dto.request.AlbumCreateRequestDto;
import com.study.petory.domain.album.dto.request.AlbumUpdateRequestDto;
import com.study.petory.domain.album.dto.request.AlbumVisibilityUpdateRequestDto;
import com.study.petory.domain.album.dto.response.AlbumGetAllResponseDto;
import com.study.petory.domain.album.dto.response.AlbumGetOneResponseDto;
import com.study.petory.domain.album.entity.Album;
import com.study.petory.domain.user.entity.UserRole;

public interface AlbumService {

	void saveAlbum(Long userId, AlbumCreateRequestDto requestDto,  List<MultipartFile> images);

	Page<AlbumGetAllResponseDto> findAllAlbum(boolean showOnlyPublic, Long userId, Pageable pageable);

	AlbumGetOneResponseDto findOneAlbum(Long userId, Long albumId);

	void updateAlbum(Long userId, Long albumId, AlbumUpdateRequestDto request);

	void updateVisibility(Long userId, Long albumId, AlbumVisibilityUpdateRequestDto request);

	void deleteAlbum(Long userId, Long albumId);

	void saveNewAlbumImage(Long userId, Long albumId, List<MultipartFile> images);

	void deleteAlbumImage(Long userId, Long imageId);

	Page<Album> findAlbumByPage(boolean showOnlyPublic, Long userId, Pageable pageable);

	Album findAlbumByAlbumId(boolean showOnlyPublic, Long albumId);

	void validateAuthor(Long userId, Album album);

	void validImageSize(List<UserRole> userRoles, List<MultipartFile> images);
}
