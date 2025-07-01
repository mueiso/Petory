package com.study.petory.domain.dailyQna.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DailyQuestionUpdateRequestDto {

	@NotBlank
	@Size(max = 85, message = "85글자 이하로 입력해주세요.")
	private String question;

	@NotBlank
	@Size(max = 10, message = "10글자 이하로 입력해주세요.")
	@Pattern(regexp = "^(0[1-9]|1[0-2])\\-(0[1-9]|[1-2][0-9]|3[0-2])$", message = "날짜 형식으로 입력해주세요.")
	private String date;
}
