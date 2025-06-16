package com.study.petory.domain.album.dto.request;

import com.study.petory.domain.album.entity.AlbumVisibility;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AlbumCreateRequestDto {

	@Size(max = 255, message = "85글자 이하로 입력해주세요.")
	private String content;

	// 기본 값: public
	private AlbumVisibility albumVisibility;
}