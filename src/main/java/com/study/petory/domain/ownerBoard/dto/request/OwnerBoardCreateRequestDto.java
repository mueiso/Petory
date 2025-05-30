package com.study.petory.domain.ownerBoard.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class OwnerBoardCreateRequestDto {

	@NotBlank
	private String title;

	@NotBlank
	private String content;

	// 사진 여러개 받기 가능
	private List<String> photoUrls;
}
