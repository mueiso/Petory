package com.study.petory.domain.ownerboard.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OwnerBoardCommentUpdateRequestDto {

	@NotBlank(message = "내용은 필수 입력 값입니다.")
	@Size(max = 80, message = "최대 80자까지 입력할 수 있습니다.")
	private String content;

}
