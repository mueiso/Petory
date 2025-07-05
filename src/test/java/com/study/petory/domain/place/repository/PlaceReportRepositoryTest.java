package com.study.petory.domain.place.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.study.petory.common.config.QueryDSLConfig;
import com.study.petory.domain.place.entity.Place;
import com.study.petory.domain.place.entity.PlaceReport;
import com.study.petory.domain.place.entity.PlaceType;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.repository.UserRepository;

@DataJpaTest
@Import(QueryDSLConfig.class)
public class PlaceReportRepositoryTest {

	@Autowired
	private PlaceReportRepository placeReportRepository;

	@Autowired
	private PlaceRepository placeRepository;

	@Autowired
	private UserRepository userRepository;

	private Place place;

	@BeforeEach
	void setUp() {
		place = Place.builder()
			.placeName("testName")
			.placeType(PlaceType.CAFE)
			.address("testAddr")
			.longitude(BigDecimal.ZERO)
			.latitude(BigDecimal.ZERO)
			.build();

		placeRepository.save(place);
	}

	@Test
	@DisplayName("장소 신고 횟수 조회 - reportResetAt이 null인 경우")
	void countPlaceReportByPlaceAndIsValidAndReportResetAtNull() {
		Place place = Place.builder()
			.placeName("testName")
			.placeType(PlaceType.CAFE)
			.address("testAddr")
			.longitude(BigDecimal.ZERO)
			.latitude(BigDecimal.ZERO)
			.build();

		placeRepository.save(place);

		long count = placeReportRepository.countPlaceReportByPlaceAndIsValidAndReportResetAt(place);

		assertAll("reportResetAt이 null인 경우 장소 신고 횟수 조회",
			() -> assertEquals(count, 0L)
		);
	}

	@Test
	@DisplayName("장소 신고 횟수 조회 - reportResetAt이 null이 아닌 경우")
	void countPlaceReportByPlaceAndIsValidAndReportResetAtNotNull() {
		User user = User.builder()
			.email("testEmail")
			.build();

		User savedUser = userRepository.save(user);

		PlaceReport placeReport = PlaceReport.builder()
			.user(savedUser)
			.place(place)
			.content("test")
			.build();

		placeReportRepository.save(placeReport);

		long count = placeReportRepository.countPlaceReportByPlaceAndIsValidAndReportResetAt(place);

		assertAll("reportResetAt이 null이 아닌 경우 장소 신고 횟수 조회",
			() -> assertEquals(count, 1L)
		);
	}
}
