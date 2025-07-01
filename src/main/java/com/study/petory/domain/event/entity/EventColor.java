package com.study.petory.domain.event.entity;

import lombok.Getter;

@Getter
public enum EventColor {
	LAVENDER("1", "#a4bdfc"),
	SAGE("2","#7ae7bf"),
	GRAPE("3","#dbadff"),
	FLAMINGO("4","#ff887c"),
	BANANA("5","#fbd75b"),
	TANGERINE("6","#ffb878"),
	PEACOCK("7","#46d6db"),
	GRAPHITE("8","#e1e1e1"),
	BLUEBERRY("9","#5484ed"),
	BASIL("10","#51b749"),
	TOMATO("11","#dc2127");


	private final String colorId;
	private final String hexColor;

	EventColor(String colorId, String hexColor) {
		this.colorId = colorId;
		this.hexColor = hexColor;
	}

	private String getColorId() {
		return colorId;
	}

	public static EventColor getEventColor(String colorId) {
		for (EventColor color : EventColor.values()) {
			if (color.getColorId().equals(colorId)) {
				return color;
			}
		}
		return BLUEBERRY;
	}
}
