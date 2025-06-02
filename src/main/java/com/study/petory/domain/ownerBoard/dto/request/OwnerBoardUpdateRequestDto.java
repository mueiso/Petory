package com.study.petory.domain.ownerBoard.dto.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class OwnerBoardUpdateRequestDto {

	@Size(max = 30, message = "최대 30자까지 입력할 수 있습니다.")
	private String title;

	@Column(columnDefinition = "TEXT")
	private String content;

	// photoUrlList 추가 예정

}
