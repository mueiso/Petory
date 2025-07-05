package com.study.petory.domain.place.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.study.petory.common.config.QueryDSLConfig;
import com.study.petory.domain.place.dto.response.PlaceGetAllResponseDto;
import com.study.petory.domain.place.entity.Place;
import com.study.petory.domain.place.entity.PlaceReview;
import com.study.petory.domain.place.entity.PlaceType;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.repository.UserRepository;

@DataJpaTest
@Import(QueryDSLConfig.class)
public class PlaceCustomRepositoryTest {

	@Autowired
	private PlaceRepository placeRepository;

	@Autowired
	private PlaceReviewRepository placeReviewRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PlaceImageRepository placeImageRepository;

	private Place place1;
	private Place place2;
	private User user1;
	private User user2;

	@BeforeEach
	void setUp() {
		place1 = Place.builder()
			.placeName("testName")
			.placeType(PlaceType.CAFE)
			.address("testAddr")
			.longitude(BigDecimal.ZERO)
			.latitude(BigDecimal.ZERO)
			.build();

		place2 = Place.builder()
			.placeName("Name")
			.placeType(PlaceType.ACCOMMODATION)
			.address("Address")
			.longitude(BigDecimal.ZERO)
			.latitude(BigDecimal.ZERO)
			.build();

		placeRepository.saveAll(List.of(place1, place2));

		user1 = User.builder()
			.email("testEmail")
			.build();

		user2 = User.builder()
			.email("Email")
			.build();

		userRepository.saveAll(List.of(user1, user2));

	}

	@Test
	@DisplayName("전체 장소 조회 - placeName만 있는 경우")
	void findAllPlaceContainsPlaceName() {
		Page<PlaceGetAllResponseDto> responseDtoPage = placeRepository.findAllPlace("test", null, null,
			PageRequest.of(0, 10));

		assertAll("placeName만 있는 경우 전체 장소 조회 검증",
			() -> assertEquals(1, responseDtoPage.getContent().size()),
			() -> assertEquals(responseDtoPage.getContent().get(0).getPlaceName(), "testName")
		);
	}

	@Test
	@DisplayName("전체 장소 조회 - address만 있는 경우")
	void findAllPlaceContainsAddress() {
		Page<PlaceGetAllResponseDto> responseDtoPage = placeRepository.findAllPlace(null, null, "test",
			PageRequest.of(0, 10));

		assertAll("address만 있는 경우 전체 장소 조회 검증",
			() -> assertEquals(1, responseDtoPage.getContent().size()),
			() -> assertEquals(responseDtoPage.getContent().get(0).getAddress(), "testAddr")
		);
	}

	@Test
	@DisplayName("전체 장소 조회 - placeName, address가 둘 다 있는 경우")
	void findAllPlaceContainsPlaceNameAndPlaceType() {
		Page<PlaceGetAllResponseDto> responseDtoPage = placeRepository.findAllPlace("test", null,
			"ress",
			PageRequest.of(0, 10));

		assertAll("placeName, address가 둘 다 있는 경우 전체 장소 조회 검증",
			() -> assertEquals(2, responseDtoPage.getContent().size()),
			() -> assertEquals(responseDtoPage.getContent().get(0).getPlaceName(), "testName"),
			() -> assertEquals(responseDtoPage.getContent().get(1).getAddress(), "Address")
		);
	}

	@Test
	@DisplayName("전체 장소 조회 - placeType만 있는 경우")
	void findAllPlaceHasPlaceType() {
		Page<PlaceGetAllResponseDto> responseDtoPage = placeRepository.findAllPlace(null, PlaceType.ACCOMMODATION, null,
			PageRequest.of(0, 10));

		assertAll("placeType만 있는 경우 전체 장소 조회 검증",
			() -> assertEquals(1, responseDtoPage.getContent().size()),
			() -> assertEquals(responseDtoPage.getContent().get(0).getPlaceType(), PlaceType.ACCOMMODATION)
		);
	}

	@Test
	@DisplayName("전체 장소 조회 - 모든 매개변수가 다 있는 경우")
	void findAllPlace() {
		Page<PlaceGetAllResponseDto> responseDtoPage = placeRepository.findAllPlace("test", PlaceType.CAFE, "ress",
			PageRequest.of(0, 10));

		assertAll("모든 매개변수가 있는 경우 전체 장소 조회 검증",
			() -> assertEquals(1, responseDtoPage.getContent().size()),
			() -> assertEquals(responseDtoPage.getContent().get(0).getPlaceName(), "testName"),
			() -> assertEquals(responseDtoPage.getContent().get(0).getPlaceType(), PlaceType.CAFE)
		);
	}

	@Test
	@DisplayName("리뷰가 포함된 장소 조회")
	void findWithReviewListByPlaceId() {
		PlaceReview placeReview1 = PlaceReview.builder()
			.user(user1)
			.place(place1)
			.content("test")
			.ratio(BigDecimal.ZERO)
			.build();

		PlaceReview placeReview2 = PlaceReview.builder()
			.user(user2)
			.place(place2)
			.content("testContent")
			.ratio(BigDecimal.ONE)
			.build();

		placeReviewRepository.saveAll(List.of(placeReview1, placeReview2));

		Optional<Place> findPlace = placeRepository.findWithReviewListByPlaceId(1L);

		assertAll("리뷰가 포함된 장소 조회 검증",
			() -> assertTrue(findPlace.isPresent())
		);
	}
}
