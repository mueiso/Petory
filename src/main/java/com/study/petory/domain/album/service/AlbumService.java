package com.study.petory.domain.album.service;

import java.awt.print.Pageable;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.study.petory.domain.album.dto.request.AlbumCreateRequestDto;

public interface AlbumService {

	void saveAlbum(Long userId, AlbumCreateRequestDto requestDto,  List<MultipartFile> images);

	void findAllAlbum(Long userId, Pageable pageable);

	void findOneAlbum();

	void findOtherAlbum();

	void updateAlbum();

	void deleteAlbum();

	void findDeletedAlbum();

	void restoreAlbum();
}
