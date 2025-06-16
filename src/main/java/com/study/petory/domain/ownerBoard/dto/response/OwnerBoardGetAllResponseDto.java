package com.study.petory.domain.ownerBoard.dto.response;

import java.util.List;

import com.study.petory.domain.ownerBoard.entity.OwnerBoard;
import com.study.petory.domain.ownerBoard.entity.OwnerBoardImage;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OwnerBoardGetAllResponseDto {

	private final Long id;

	private final String title;

	private final String content;

	private final String imageUrl;

	public static OwnerBoardGetAllResponseDto from(OwnerBoard ownerBoard) {
		// 이미지 리스트가 비었는지 확인하고, 첫 번째 이미지 URL을 가져옴
		String firstImageUrl = null;

		List<OwnerBoardImage> images = ownerBoard.getImages();
		if (images != null & !images.isEmpty()) {
			firstImageUrl = images.get(0).getUrl();
		}

		return new OwnerBoardGetAllResponseDto(
			ownerBoard.getId(),
			ownerBoard.getTitle(),
			ownerBoard.getContent(),
			firstImageUrl
		);
	}
}
