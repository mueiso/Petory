package com.study.petory.domain.pet.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PetSize {

	LARGE_SIZED_ANIMAL("대동물"),
	MEDIUM_SIZED_ANIMAL("중간 크기 동물"),
	SMALL_SIZED_ANIMAL("소동물");

	private final String description;
}
