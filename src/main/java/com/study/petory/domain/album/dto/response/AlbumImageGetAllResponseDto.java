package com.study.petory.domain.album.dto.response;

import java.time.LocalDateTime;

import com.study.petory.domain.album.entity.Album;
import com.study.petory.domain.album.entity.AlbumImage;

import lombok.Getter;

@Getter
public class AlbumImageGetAllResponseDto {

	private final String url;

	private final LocalDateTime createdAt;

	private AlbumImageGetAllResponseDto(String url, LocalDateTime createdAt) {
		this.url = url;
		this.createdAt = createdAt;
	}

	public static AlbumImageGetAllResponseDto from(AlbumImage albumImage) {
		return new AlbumImageGetAllResponseDto(
			albumImage.getUrl(),
			albumImage.getCreatedAt()
		);
	}
}
