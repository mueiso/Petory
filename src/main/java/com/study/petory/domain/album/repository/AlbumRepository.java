package com.study.petory.domain.album.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.study.petory.domain.album.entity.Album;

public interface AlbumRepository extends JpaRepository<Album, Long>, AlbumQueryRepository {
}
