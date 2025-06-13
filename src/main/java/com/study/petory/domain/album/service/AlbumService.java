package com.study.petory.domain.album.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.study.petory.domain.album.dto.request.AlbumCreateRequestDto;
import com.study.petory.domain.album.dto.response.AlbumGetAllResponseDto;
import com.study.petory.domain.album.dto.response.AlbumGetOneResponseDto;
import com.study.petory.domain.album.entity.Album;

public interface AlbumService {

	void saveAlbum(Long userId, AlbumCreateRequestDto requestDto,  List<MultipartFile> images);

	Page<AlbumGetAllResponseDto> findUserAllAlbum(Long userId, Pageable pageable);

	AlbumGetOneResponseDto findOneAlbum(Long albumId);

	Page<AlbumGetAllResponseDto> findAllAlbum(Pageable pageable);

	void updateAlbum();

	void deleteAlbum();

	void findDeletedAlbum();

	void restoreAlbum();

	Page<Album> findPageAlbum(Long userId, Pageable pageable);
}
