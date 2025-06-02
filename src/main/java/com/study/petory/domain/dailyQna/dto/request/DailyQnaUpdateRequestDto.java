package com.study.petory.domain.dailyQna.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DailyQnaUpdateRequestDto {

	@NotBlank
	@Size(max = 255, message = "70글자까지 입력이 가능합니다")
	private String answer;
}
