package com.study.petory.domain.album.dto.response;

import java.time.LocalDateTime;

import com.study.petory.domain.album.entity.Album;

import lombok.Getter;

@Getter
public class AlbumGetAllResponseDto {

	private Long albumId;

	private String url;

	private LocalDateTime createdAt;

	private AlbumGetAllResponseDto(Long albumId, String url, LocalDateTime createdAt) {
		this.albumId = albumId;
		this.url = url;
		this.createdAt = createdAt;
	}

	public AlbumGetAllResponseDto from(Album album) {
		return new AlbumGetAllResponseDto(
			this.albumId = album.getId(),
			this.url = album.getFirstUrl(),
			this.createdAt = album.getCreatedAt()
		);
	}
}
