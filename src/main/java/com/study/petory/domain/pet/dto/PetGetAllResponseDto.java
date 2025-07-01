package com.study.petory.domain.pet.dto;

import com.study.petory.domain.pet.entity.Pet;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PetGetAllResponseDto {

	private Long id;
	private String name;

	@Builder
	public PetGetAllResponseDto(Long id, String name) {
		this.id = id;
		this.name = name;
	}

	public static PetGetAllResponseDto of(Pet pet) {
		return PetGetAllResponseDto.builder()
			.id(pet.getId())
			.name(pet.getName())
			.build();
	}
}
