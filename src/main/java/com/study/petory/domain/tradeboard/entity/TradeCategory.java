package com.study.petory.domain.tradeboard.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TradeCategory {

	FOOD_AND_TREATS("사료와 간식"),
	TOYS("장난감"),
	HOUSES_AND_CARRIERS("집과 이동장"),
	FEEDERS_AND_TOILETS("급식기와 화장실"),
	GROOMING_AND_CARE_SUPPLIES("미용 용품"),
	CLOTHES_AND_ACCESSORIES("옷과 악세사리"),
	HEALTH("건강 용품");

	private final String description;
}