package com.study.petory.domain.album.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.study.petory.domain.album.entity.Album;

public interface AlbumCustomRepository {

	Page<Album> findAllAlbum(boolean showOnlyPublic, Long userId, Pageable pageable);

	Optional<Album> findOneAlbumByUser(boolean showOnlyPublic, Long albumId);

	boolean existTodayAlbum(Long userId);
}

