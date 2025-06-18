package com.study.petory.domain.place.entity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class PlaceTest {

	// @Test
	// @DisplayName("평점 계산")
	// void updateRatio() {
	//
	// 	Place place = Place.builder().build();
	//
	// 	ReflectionTestUtils.setField(place, "id", 1L);
	//
	// 	PlaceReview placeReview1 = PlaceReview.builder().ratio(new BigDecimal("4")).build();
	// 	PlaceReview placeReview2 = PlaceReview.builder().ratio(new BigDecimal("1")).build();
	// 	PlaceReview placeReview3 = PlaceReview.builder().ratio(new BigDecimal("2")).build();
	//
	// 	place.getPlaceReviewList().addAll(List.of(placeReview1, placeReview2, placeReview3));
	//
	// 	place.updateRatio();
	//
	// 	assertAll("평점 계산 로직 검증",
	// 		() -> assertEquals(new BigDecimal("2.3"), place.getRatio())
	// 	);
	// }
}