package com.study.petory.domain.pet.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.study.petory.domain.pet.entity.PetSize;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PetCreateResponseDto {

	private final Long id;

	private String name;

	private PetSize size;

	private String species;

	private String gender;

	private String birthday;

	private final List<String> imageUrls;

	private final LocalDateTime createdAt;
}
