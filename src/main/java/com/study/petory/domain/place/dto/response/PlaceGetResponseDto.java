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

	private final String address;

	private final BigDecimal ratio;

	private final List<String> imageUrls;

	private final BigDecimal latitude;

	private final BigDecimal longitude;

	private final List<PlaceReviewGetResponseDto> placeReviewList;

	private final Long likeCount;

	private PlaceGetResponseDto(Place place, List<String> imageUrls,
		List<PlaceReviewGetResponseDto> placeReviewGetResponseDto) {
		this.id = place.getId();
		this.placeName = place.getPlaceName();
		this.placeInfo = place.getPlaceInfo();
		this.placeType = place.getPlaceType();
		this.address = place.getAddress();
		this.ratio = place.getRatio();
		this.imageUrls = imageUrls;
		this.latitude = place.getLatitude();
		this.longitude = place.getLongitude();
		this.placeReviewList = placeReviewGetResponseDto;
		this.likeCount = place.getLikeCount();
	}

	public static PlaceGetResponseDto of(Place place, List<String> imageUrls,
		List<PlaceReviewGetResponseDto> placeReviewGetResponseDto) {
		return new PlaceGetResponseDto(place, imageUrls, placeReviewGetResponseDto);
	}
}
