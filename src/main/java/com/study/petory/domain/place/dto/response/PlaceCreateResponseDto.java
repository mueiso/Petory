package com.study.petory.domain.place.dto.response;

import java.math.BigDecimal;

import com.study.petory.domain.place.entity.Place;
import com.study.petory.domain.place.entity.PlaceType;

import lombok.Getter;

@Getter
public class PlaceCreateResponseDto {

	private final Long id;

	private final String placeName;

	private final String placeInfo;

	private final PlaceType placeType;

	private final BigDecimal ratio;

	private final BigDecimal latitude;

	private final BigDecimal longitude;

	private PlaceCreateResponseDto(Place place) {
		this.id = place.getId();
		this.placeName = place.getPlaceName();
		this.placeInfo = place.getPlaceName();
		this.placeType = place.getPlaceType();
		this.ratio = place.getRatio();
		this.latitude = place.getLatitude();
		this.longitude = place.getLongitude();
	}

	public static PlaceCreateResponseDto fromPlace(Place place) {
		return new PlaceCreateResponseDto(place);
	}
}
