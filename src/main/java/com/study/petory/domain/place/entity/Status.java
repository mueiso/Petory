package com.study.petory.domain.place.entity;

import lombok.Getter;

@Getter
public enum Status {
	ACTIVE("영업중"),
	INACTIVE("영업 중지"),
	DELETED("폐업");

	private final String displayName;

	Status(String displayName) {
		this.displayName = displayName;
	}
}
