package com.study.petory.domain.album.dto.request;

import com.study.petory.common.util.ValidEnum;
import com.study.petory.domain.album.entity.AlbumVisibility;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AlbumVisibilityUpdateRequestDto {

	@NotNull(message = "공개 여부를 입력해주세요")
	@ValidEnum(enumClass = AlbumVisibility.class, ignoreCase = true, message = "지원하지 않는 공개 상태입니다.")
	private AlbumVisibility albumVisibility;
}
