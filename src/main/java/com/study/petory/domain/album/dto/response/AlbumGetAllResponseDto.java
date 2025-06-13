package com.study.petory.domain.album.dto.response;

import java.time.LocalDateTime;

import com.study.petory.domain.album.entity.Album;

import lombok.Getter;

@Getter
public class AlbumGetAllResponseDto {

	private final Long albumId;

	private final String url;

	private final LocalDateTime createdAt;

	private AlbumGetAllResponseDto(Long albumId, String url, LocalDateTime createdAt) {
		this.albumId = albumId;
		this.url = url;
		this.createdAt = createdAt;
	}

	public static AlbumGetAllResponseDto from(Album album) {
		return new AlbumGetAllResponseDto(
			album.getId(),
			album.getFirstUrl(),
			album.getCreatedAt()
		);
	}
}
