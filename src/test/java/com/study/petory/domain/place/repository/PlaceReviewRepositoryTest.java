package com.study.petory.domain.place.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.study.petory.common.config.QueryDSLConfig;
import com.study.petory.domain.place.entity.Place;
import com.study.petory.domain.place.entity.PlaceReview;
import com.study.petory.domain.place.entity.PlaceType;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.repository.UserRepository;

@DataJpaTest
@Import(QueryDSLConfig.class)
public class PlaceReviewRepositoryTest {

	@Autowired
	private PlaceReviewRepository placeReviewRepository;

	@Autowired
	private PlaceRepository placeRepository;

	@Autowired
	private UserRepository userRepository;

	@Test
	@DisplayName("평점 계산")
	void calculateAvgRatioByPlaceId() {
		Place place = Place.builder()
			.placeName("testName")
			.placeType(PlaceType.CAFE)
			.address("testAddr")
			.longitude(BigDecimal.ZERO)
			.latitude(BigDecimal.ZERO)
			.build();

		placeRepository.save(place);

		User user = User.builder()
			.email("testEmail")
			.build();

		userRepository.save(user);

		PlaceReview placeReview1 = PlaceReview.builder()
			.user(user)
			.place(place)
			.ratio(BigDecimal.ONE)
			.content("test")
			.build();

		PlaceReview placeReview2 = PlaceReview.builder()
			.user(user)
			.place(place)
			.ratio(BigDecimal.TEN)
			.content("test")
			.build();

		PlaceReview placeReview3 = PlaceReview.builder()
			.user(user)
			.place(place)
			.ratio(BigDecimal.ONE)
			.content("test")
			.build();

		placeReviewRepository.saveAll(List.of(placeReview1, placeReview2, placeReview3));

		Double calculated = placeReviewRepository.calculateAvgRatioByPlaceId(place.getId());

		assertAll("평점 계산 검증",
			() -> assertEquals(4, calculated)
		);
	}
}
