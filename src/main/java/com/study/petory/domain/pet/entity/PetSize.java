package com.study.petory.domain.pet.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PetSize {

	LARGE("대동물"),
	MEDIUM("중간 크기 동물"),
	SMALL("소동물");

	private final String description;
}
