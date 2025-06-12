package com.study.petory.domain.ownerBoard.dto.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OwnerBoardUpdateRequestDto {

	@NotBlank
	@Size(max = 30, message = "최대 30자까지 입력할 수 있습니다.")
	private String title;

	@NotBlank
	@Column(columnDefinition = "TEXT")
	private String content;

}
