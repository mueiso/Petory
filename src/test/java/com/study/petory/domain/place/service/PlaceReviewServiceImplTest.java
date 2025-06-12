package com.study.petory.domain.place.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.study.petory.common.exception.CustomException;
import com.study.petory.common.exception.enums.ErrorCode;
import com.study.petory.domain.place.dto.request.PlaceReviewCreateRequestDto;
import com.study.petory.domain.place.dto.response.PlaceReviewCreateResponseDto;
import com.study.petory.domain.place.entity.Place;
import com.study.petory.domain.place.entity.PlaceReview;
import com.study.petory.domain.place.repository.PlaceReviewRepository;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class PlaceReviewServiceImplTest {

	@Mock
	private PlaceReviewRepository placeReviewRepository;

	@Mock
	private PlaceService placeService;

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private PlaceReviewServiceImpl placeReviewServiceImpl;

	@Test
	@DisplayName("장소 리뷰 등록 - 중복 리뷰가 아닌 경우")
	void savePlaceReview() {
		Place place = Place.builder()
			.build();

		ReflectionTestUtils.setField(place, "id", 1L);

		User user = new User();

		ReflectionTestUtils.setField(user, "id", 1L);

		PlaceReviewCreateRequestDto dto = new PlaceReviewCreateRequestDto("testContent", BigDecimal.ZERO);

		when(placeService.findPlaceWithPlaceReviewByPlaceId(1L)).thenReturn(place);
		when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		when(placeReviewRepository.findByUserAndPlace(user, place)).thenReturn(Optional.empty());

		when(placeReviewRepository.save(any(PlaceReview.class)))
			.thenAnswer(invocation -> {
				PlaceReview placeReview = invocation.getArgument(0);
				ReflectionTestUtils.setField(placeReview, "id", 1L);
				return placeReview;
			});

		PlaceReviewCreateResponseDto responseDto = placeReviewServiceImpl.savePlaceReview(1L, dto);

		assertAll("장소 리뷰 등록 로직 검증",
			() -> assertEquals("testContent", responseDto.getContent()),
			() -> assertEquals(BigDecimal.ZERO, responseDto.getRatio())
		);
	}

	@Test
	void saveDuplicatedPlaceReview() {
	}

	@Test
	void updatePlaceReview() {
	}

	@Test
	void restorePlaceReview() {
	}

	@Test
	void deletePlaceReview() {
	}
}