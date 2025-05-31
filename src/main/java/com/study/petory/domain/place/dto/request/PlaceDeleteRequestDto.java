package com.study.petory.domain.place.dto.request;

import com.study.petory.domain.place.entity.Status;

import lombok.Getter;

@Getter
public class PlaceDeleteRequestDto {

	private final Status status;

	public PlaceDeleteRequestDto(Status status) {
		this.status = status;
	}
}
