package com.study.petory.domain.album.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AlbumUpdateRequestDto {

	@Size(max = 255, message = "255글자 이하로 입력해주세요.")
	private String content;
}
