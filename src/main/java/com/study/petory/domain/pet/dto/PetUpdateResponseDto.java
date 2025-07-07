package com.study.petory.domain.pet.dto;

import java.util.List;

import com.study.petory.domain.pet.entity.Pet;
import com.study.petory.domain.pet.entity.PetSize;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PetUpdateResponseDto {

	private Long id;
	private String name;
	private PetSize size;
	private String species;
	private String gender;
	private String birthday;
	private List<String> imageUrls;

	public static PetUpdateResponseDto of(Pet pet, List<String> imageUrls) {
		return new PetUpdateResponseDto(
			pet.getId(),
			pet.getName(),
			pet.getSize(),
			pet.getSpecies(),
			pet.getGender(),
			pet.getBirthday(),
			imageUrls
		);
	}
}
