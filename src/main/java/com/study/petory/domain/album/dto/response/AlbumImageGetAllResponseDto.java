package com.study.petory.domain.album.dto.response;

import java.time.LocalDateTime;

import com.study.petory.domain.album.entity.Album;
import com.study.petory.domain.album.entity.AlbumImage;

import lombok.Getter;

@Getter
public class AlbumImageGetAllResponseDto {

	private final Long albumImageId;

	private final String url;

	private final LocalDateTime createdAt;

	private AlbumImageGetAllResponseDto(Long albumImageId, String url, LocalDateTime createdAt) {
		this.albumImageId = albumImageId;
		this.url = url;
		this.createdAt = createdAt;
	}

	public static AlbumImageGetAllResponseDto from(AlbumImage albumImage) {
		return new AlbumImageGetAllResponseDto(
			albumImage.getId(),
			albumImage.getUrl(),
			albumImage.getCreatedAt()
		);
	}
}
