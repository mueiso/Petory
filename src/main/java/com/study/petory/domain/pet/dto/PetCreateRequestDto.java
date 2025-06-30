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

	private PetSize size;

	private String species;

	private String gender;

	private String birthday;
}
