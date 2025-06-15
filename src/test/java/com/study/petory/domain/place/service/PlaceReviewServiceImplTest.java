package com.study.petory.domain.place.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import com.study.petory.common.exception.CustomException;
import com.study.petory.common.exception.enums.ErrorCode;
import com.study.petory.common.security.CustomPrincipal;
import com.study.petory.domain.place.dto.request.PlaceReviewCreateRequestDto;
import com.study.petory.domain.place.dto.request.PlaceReviewUpdateRequestDto;
import com.study.petory.domain.place.dto.response.PlaceReviewCreateResponseDto;
import com.study.petory.domain.place.dto.response.PlaceReviewUpdateResponseDto;
import com.study.petory.domain.place.entity.Place;
import com.study.petory.domain.place.entity.PlaceReview;
import com.study.petory.domain.place.repository.PlaceReviewRepository;
import com.study.petory.domain.user.entity.Role;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.entity.UserRole;
import com.study.petory.domain.user.repository.UserRepository;
import com.study.petory.domain.user.service.UserService;

@ExtendWith(MockitoExtension.class)
class PlaceReviewServiceImplTest {

	@Mock
	private PlaceReviewRepository placeReviewRepository;

	@Mock
	private PlaceService placeService;

	@Mock
	private UserService userService;

	@InjectMocks
	private PlaceReviewServiceImpl placeReviewServiceImpl;

	@BeforeEach
	void setUp() {
		List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));

		CustomPrincipal principal = new CustomPrincipal(1L, "admin@example.com", "관리자", authorities);

		UsernamePasswordAuthenticationToken authToken =
			new UsernamePasswordAuthenticationToken(principal, null, authorities);

		SecurityContextHolder.getContext().setAuthentication(authToken);
	}

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
		when(userService.getUserById(1L)).thenReturn(user);
		when(placeReviewRepository.findByUserAndPlace(user, place)).thenReturn(Optional.empty());

		when(placeReviewRepository.save(any(PlaceReview.class)))
			.thenAnswer(invocation -> {
				PlaceReview placeReview = invocation.getArgument(0);
				ReflectionTestUtils.setField(placeReview, "id", 1L);
				return placeReview;
			});

		PlaceReviewCreateResponseDto responseDto = placeReviewServiceImpl.savePlaceReview(1L, 1L, dto);

		assertAll("장소 리뷰 등록 로직 검증",
			() -> assertEquals("testContent", responseDto.getContent()),
			() -> assertEquals(BigDecimal.ZERO, responseDto.getRatio())
		);
	}

	@Test
	@DisplayName("장소 리뷰 등록 - 중복 리뷰인 경우")
	void saveDuplicatedPlaceReview() {

		Place place = Place.builder()
			.build();

		ReflectionTestUtils.setField(place, "id", 1L);

		User user = new User();

		ReflectionTestUtils.setField(user, "id", 1L);

		PlaceReviewCreateRequestDto dto = new PlaceReviewCreateRequestDto("testContent", BigDecimal.ZERO);

		PlaceReview placeReview = PlaceReview.builder().build();

		when(placeService.findPlaceWithPlaceReviewByPlaceId(1L)).thenReturn(place);
		when(userService.getUserById(1L)).thenReturn(user);
		when(placeReviewRepository.findByUserAndPlace(user, place)).thenReturn(Optional.of(placeReview));

		// 예외 발생을 기대하는 테스트 코드
		// assertThrows는 실제 발생한 예외를 리턴해준다
		CustomException customException = assertThrows(
			CustomException.class,
			() -> placeReviewServiceImpl.savePlaceReview(1L, 1L, dto)
		);

		assertEquals(ErrorCode.DUPLICATE_REVIEW, customException.getErrorCode());
	}

	@Test
	@DisplayName("장소 리뷰 수정")
	void updatePlaceReview() {

		Place place = Place.builder().build();

		User user = new User();

		ReflectionTestUtils.setField(place, "id", 1L);
		ReflectionTestUtils.setField(user, "id", 1L);

		PlaceReview placeReview = PlaceReview.builder()
			.place(place)
			.user(user)
			.content("testContent")
			.ratio(BigDecimal.ONE)
			.build();

		ReflectionTestUtils.setField(placeReview, "id", 1L);

		PlaceReviewUpdateRequestDto dto = new PlaceReviewUpdateRequestDto("updateTestContent", BigDecimal.ZERO);

		when(placeService.findPlaceWithPlaceReviewByPlaceId(1L)).thenReturn(place);
		when(placeReviewRepository.findById(1L)).thenReturn(Optional.of(placeReview));

		PlaceReviewUpdateResponseDto responseDto = placeReviewServiceImpl.updatePlaceReview(1L, 1L, 1L,
			dto);

		assertAll("장소 리뷰 수정 로직 검증",
			() -> assertEquals("updateTestContent", responseDto.getContent()),
			() -> assertEquals(BigDecimal.ZERO, responseDto.getRatio())
		);
	}

	@Test
	@DisplayName("장소 리뷰 복구")
	void restorePlaceReview() {

		Place place = Place.builder().build();

		ReflectionTestUtils.setField(place, "id", 1L);

		PlaceReview placeReview = PlaceReview.builder().build();

		ReflectionTestUtils.setField(placeReview, "deletedAt", LocalDateTime.now());

		User user = new User();

		ReflectionTestUtils.setField(user, "id", 1L);
		ReflectionTestUtils.setField(user, "userRole", Role.ADMIN);

		when(placeService.findPlaceWithPlaceReviewByPlaceId(1L)).thenReturn(place);
		when(placeReviewRepository.findById(1L)).thenReturn(Optional.of(placeReview));

		placeReviewServiceImpl.restorePlaceReview(1L, 1L, 1L);

		assertAll("장소 리뷰 복구 로직 검증",
			() -> assertNull(placeReview.getDeletedAt())
		);
	}

	@Test
	@DisplayName("장소 리뷰 삭제")
	void deletePlaceReview() {

		Place place = Place.builder().build();

		ReflectionTestUtils.setField(place, "id", 1L);

		PlaceReview placeReview = PlaceReview.builder().build();

		ReflectionTestUtils.setField(placeReview, "id", 1L);

		when(placeService.findPlaceWithPlaceReviewByPlaceId(1L)).thenReturn(place);
		when(placeReviewRepository.findById(1L)).thenReturn(Optional.of(placeReview));

		placeReviewServiceImpl.deletePlaceReview(1L, 1L, 1L);

		assertAll("장소 리뷰 삭제 로직 검증",
			() -> assertNotNull(placeReview.getDeletedAt())
		);
	}
}