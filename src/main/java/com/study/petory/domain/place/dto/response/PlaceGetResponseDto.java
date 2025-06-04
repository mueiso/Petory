package com.study.petory.domain.place.dto.response;

import java.math.BigDecimal;
import java.util.List;

import com.study.petory.domain.place.entity.Place;
import com.study.petory.domain.place.entity.PlaceType;

import lombok.Getter;

@Getter
public class PlaceGetResponseDto {

	private final Long id;

	private final String placeName;

	private final String placeInfo;

	private final PlaceType placeType;

	private final BigDecimal ratio;

	// private final String photoList; // 이부분도 CreateResponseDto랑 비슷하게 하면 될듯

	private final BigDecimal latitude;

	private final BigDecimal longitude;

	private final List<PlaceReviewGetResponseDto> placeReviewList;

	private PlaceGetResponseDto(Place place, List<PlaceReviewGetResponseDto> placeReviewGetResponseDto) {
		this.id = place.getId();
		this.placeName = place.getPlaceName();
		this.placeInfo = place.getPlaceInfo();
		this.placeType = place.getPlaceType();
		this.ratio = place.getRatio();
		this.latitude = place.getLatitude();
		this.longitude = place.getLongitude();
		this.placeReviewList = placeReviewGetResponseDto;
	}

	public static PlaceGetResponseDto from(Place place, List<PlaceReviewGetResponseDto> placeReviewGetResponseDto) {
		return new PlaceGetResponseDto(place, placeReviewGetResponseDto);
	}
}
