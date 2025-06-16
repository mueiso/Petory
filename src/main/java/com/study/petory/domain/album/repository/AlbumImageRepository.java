package com.study.petory.domain.album.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.study.petory.domain.album.entity.AlbumImage;

public interface AlbumImageRepository extends JpaRepository<AlbumImage, Long> {
}
