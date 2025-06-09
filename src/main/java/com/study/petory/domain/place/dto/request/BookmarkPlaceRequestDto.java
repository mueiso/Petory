package com.study.petory.domain.place.dto.request;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookmarkPlaceRequestDto {

	@JsonProperty("name")
	private String placeName;

	@JsonProperty("address")
	private String address;

	@JsonProperty("px")
	private BigDecimal longitude;

	@JsonProperty("py")
	private BigDecimal latitude;

	@JsonProperty("mcidName")
	private String placeType;
}
