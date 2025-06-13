package com.study.petory.domain.album.dto.response;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.study.petory.domain.album.entity.Album;
import com.study.petory.domain.album.entity.AlbumImage;

import lombok.Getter;

@Getter
public class AlbumGetOneResponseDto {

	private final Long albumId;

	private final List<AlbumImageGetAllResponseDto> albumImageList;

	private final LocalDateTime createdAt;

	private AlbumGetOneResponseDto(Long albumId, List<AlbumImageGetAllResponseDto> albumImageList,
		LocalDateTime createdAt) {
		this.albumId = albumId;
		this.albumImageList = albumImageList;
		this.createdAt = createdAt;
	}

	public static AlbumGetOneResponseDto from(Album album) {
		List<AlbumImageGetAllResponseDto> imageDtoList = new ArrayList<>();
		imageDtoList = album.getAlbumImageList().stream().map(AlbumImageGetAllResponseDto::from).toList();
		return new AlbumGetOneResponseDto(
			album.getId(),
			imageDtoList,
			album.getCreatedAt()
		);
	}
}
