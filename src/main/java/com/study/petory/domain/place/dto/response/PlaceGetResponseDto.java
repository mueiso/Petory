package com.study.petory.domain.place.dto.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import com.study.petory.domain.place.entity.Place;
import com.study.petory.domain.place.entity.PlaceType;

import lombok.Builder;
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

	@Builder
	public PlaceGetResponseDto(Long id, String placeName, String placeInfo, PlaceType placeType, BigDecimal ratio,
		BigDecimal latitude, BigDecimal longitude, List<PlaceReviewGetResponseDto> placeReviewList) {
		this.id = id;
		this.placeName = placeName;
		this.placeInfo = placeInfo;
		this.placeType = placeType;
		this.ratio = ratio;
		this.latitude = latitude;
		this.longitude = longitude;
		this.placeReviewList = placeReviewList;
	}

	public static PlaceGetResponseDto from(Place place) {
		return PlaceGetResponseDto.builder()
			.id(place.getId())
			.placeName(place.getPlaceName())
			.placeInfo(place.getPlaceInfo())
			.placeType(place.getPlaceType())
			.ratio(place.getRatio())
			.latitude(place.getLatitude())
			.longitude(place.getLongitude())
			.placeReviewList(place.getPlaceReviewList().stream()
				.filter(placeReview -> placeReview.getDeletedAt() == null)
				.map(PlaceReviewGetResponseDto::from)
				.collect(Collectors.toList()))
			.build();
	}
}
