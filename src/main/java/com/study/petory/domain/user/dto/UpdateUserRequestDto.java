package com.study.petory.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateUserRequestDto {

	@NotBlank(message = "닉네임은 필수 입력 값입니다.")
	private String nickname;
}
