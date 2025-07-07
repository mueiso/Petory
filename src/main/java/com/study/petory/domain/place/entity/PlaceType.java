package com.study.petory.domain.place.entity;

import lombok.Getter;

@Getter
public enum PlaceType {
	CAFE("카페"),
	RESTAURANT("음식점"),
	HOSPITAL("병원"),
	STORE("애견 용품점"),
	BEAUTY("애견 미용실"),
	SCHOOL("애견 유치원"),
	ACCOMMODATION("숙박"),
	BAR("주점"),
	CAMPING("캠핑, 야영장"),
	ETC("기타");

	private final String displayName;

	PlaceType(String displayName) {
		this.displayName = displayName;
	}
}
