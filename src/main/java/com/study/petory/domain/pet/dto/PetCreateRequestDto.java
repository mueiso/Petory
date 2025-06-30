package com.study.petory.domain.pet.dto;

import com.study.petory.domain.pet.entity.PetSize;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PetCreateRequestDto {

	@NotBlank(message = "이름은 필수 입력값 입니다.")
	@Size(max = 30, message = "최대 30자까지 입력할 수 있습니다.")
	private String name;

	@NotBlank(message = "크기는 필수 입력값 입니다. (LARGE(대동물), MEDIUM(중간 크기 동물), SMALL(소동물)")
	private PetSize size;

	@NotBlank(message = "종은 필수 입력값 입니다.")
	private String species;

	private String gender;

	private String birthday;
}
