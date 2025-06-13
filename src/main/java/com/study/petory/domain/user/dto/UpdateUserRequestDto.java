package com.study.petory.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateUserRequestDto {

	@Size(max = 30, message = "최대 입력 가능한 글자수는 30자 입니다.")
	private String nickname;

	@NotBlank(message = "전화번호는 필수 입력 값입니다.")
	private String mobileNum;
}
