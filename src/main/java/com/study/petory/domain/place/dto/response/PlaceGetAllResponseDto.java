package com.study.petory.domain.place.dto.response;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.study.petory.domain.place.entity.Place;
import com.study.petory.domain.place.entity.PlaceType;

import lombok.Getter;

@Getter
@JsonTypeInfo(
	use = JsonTypeInfo.Id.CLASS,
	include = JsonTypeInfo.As.PROPERTY,
	property = "@class"
)
public class PlaceGetAllResponseDto {

	private final Long id;

	private final String placeName;

	private final String placeInfo;

	private final PlaceType placeType;

	private final String address;

	private final BigDecimal ratio;

	private final List<String> imageUrls;

	private final BigDecimal latitude;

	private final BigDecimal longitude;

	private final Long likeCount;

	public PlaceGetAllResponseDto(Long id, String placeName, String placeInfo, PlaceType placeType, String address,
		BigDecimal ratio, List<String> imageUrls, BigDecimal latitude, BigDecimal longitude, Long likeCount) {
		this.id = id;
		this.placeName = placeName;
		this.placeInfo = placeInfo;
		this.placeType = placeType;
		this.address = address;
		this.ratio = ratio;
		this.imageUrls = imageUrls;
		this.latitude = latitude;
		this.longitude = longitude;
		this.likeCount = likeCount;
	}

	public static PlaceGetAllResponseDto of(Place place, List<String> imageUrls) {
		return new PlaceGetAllResponseDto(
			place.getId(),
			place.getPlaceName(),
			place.getPlaceInfo(),
			place.getPlaceType(),
			place.getAddress(),
			place.getRatio(),
			imageUrls,
			place.getLatitude(),
			place.getLongitude(),
			place.getLikeCount()
		);
	}
}
