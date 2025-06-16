package com.study.petory.domain.album.dto.response;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.study.petory.domain.album.entity.Album;
import com.study.petory.domain.album.entity.AlbumImage;

import lombok.Getter;

@Getter
public class AlbumGetOneResponseDto {

	private final Long albumId;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private final String content;

	private final List<AlbumImageGetAllResponseDto> albumImageList;

	private final LocalDateTime createdAt;

	private AlbumGetOneResponseDto(Long albumId, String content, List<AlbumImageGetAllResponseDto> albumImageList,
		LocalDateTime createdAt) {
		this.albumId = albumId;
		this.content = content;
		this.albumImageList = albumImageList;
		this.createdAt = createdAt;
	}

	public static AlbumGetOneResponseDto from(Album album) {
		List<AlbumImageGetAllResponseDto> imageDtoList = new ArrayList<>();
		imageDtoList = album.getAlbumImageList().stream().map(AlbumImageGetAllResponseDto::from).toList();
		return new AlbumGetOneResponseDto(
			album.getId(),
			album.getContent(),
			imageDtoList,
			album.getCreatedAt()
		);
	}
}
