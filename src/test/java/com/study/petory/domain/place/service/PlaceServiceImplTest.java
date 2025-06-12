package com.study.petory.domain.place.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import com.study.petory.domain.place.dto.request.PlaceCreateRequestDto;
import com.study.petory.domain.place.dto.request.PlaceStatusChangeRequestDto;
import com.study.petory.domain.place.dto.request.PlaceUpdateRequestDto;
import com.study.petory.domain.place.dto.response.PlaceCreateResponseDto;
import com.study.petory.domain.place.dto.response.PlaceGetAllResponseDto;
import com.study.petory.domain.place.dto.response.PlaceGetResponseDto;
import com.study.petory.domain.place.dto.response.PlaceUpdateResponseDto;
import com.study.petory.domain.place.entity.Place;
import com.study.petory.domain.place.entity.PlaceReview;
import com.study.petory.domain.place.entity.PlaceStatus;
import com.study.petory.domain.place.entity.PlaceType;
import com.study.petory.domain.place.repository.PlaceRepository;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class PlaceServiceImplTest {

	@Mock
	private PlaceRepository placeRepository;

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private PlaceServiceImpl placeServiceImpl;

	@Test
	@DisplayName("장소 등록")
	void savePlace() {
		User user = new User();

		PlaceCreateRequestDto dto = new PlaceCreateRequestDto("testName", null,
			PlaceType.ACCOMMODATION, "testAddress", BigDecimal.ONE, BigDecimal.ONE);

		when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

		// 서비스 로직에서 placeRepository에서 save했을 때의 변수를 return에 사용하지 않는다
		// 근데 서비스 로직에서는 실제로 insert 쿼리가 발생하기 때문에 상관없지만
		// 테스트 코드에선 실제로 insert 쿼리가 발생하는게 아니기 때문에 id가 null로 들어간다
		// 따라서 thenAnswer을 통해 id를 강제적으로 넣어주는 방법을 사용해야 된다
		// invocationOnMock.getArgument(0) 이 부분은 placeRepository.save();의 매개변수의 0번째 인자를 가져오는 것!
		// 즉, 서비스 코드 상으로 봤을 때 place를 가져오는 것!
		// 실제로 insert문이 발생하는게 아니기 때문에 id가 null이기 때문에 강제적으로 1L을 주입!
		when(placeRepository.save(any(Place.class))).thenAnswer(invocationOnMock -> {
			Place savedPlace = invocationOnMock.getArgument(0);
			ReflectionTestUtils.setField(savedPlace, "id", 1L);
			return savedPlace;
		});

		PlaceCreateResponseDto responseDto = placeServiceImpl.savePlace(dto);

		assertAll("저장 로직 검증",
			() -> assertEquals(1L, responseDto.getId()),
			() -> assertEquals("testName", responseDto.getPlaceName()),
			() -> assertEquals(PlaceType.ACCOMMODATION, responseDto.getPlaceType()),
			() -> assertEquals(BigDecimal.ONE, responseDto.getLatitude()),
			() -> assertEquals(BigDecimal.ONE, responseDto.getLongitude())
		);
	}

	@Test
	@DisplayName("전체 장소 조회 - placeName과 placeType이 모두 입력되는 경우")
	void findAllPlaceWithPlaceNameAndPlaceType() {

		String placeName = "test";
		PlaceType placeType = PlaceType.ACCOMMODATION;
		Pageable pageable = PageRequest.of(0, 10);

		// 이 테스트에서 PlaceGetAllResponseDto는 테스트 대상이 아니므로 mock 객체로 만들어 사용
		PlaceGetAllResponseDto dto = mock(PlaceGetAllResponseDto.class);
		Page<PlaceGetAllResponseDto> page = new PageImpl<>(List.of(dto));

		when(placeRepository.findAllByPlaceNameContainingAndPlaceType(placeName, placeType, pageable)).thenReturn(page);

		Page<PlaceGetAllResponseDto> findAllPlace = placeServiceImpl.findAllPlace(placeName, placeType, pageable);

		assertAll("파람이 모두 있을 경우 조회 로직 검증",
			() -> assertEquals(1, findAllPlace.getContent().size()),
			() -> verify(placeRepository).findAllByPlaceNameContainingAndPlaceType(placeName, placeType, pageable)
		);
	}

	@Test
	@DisplayName("전체 장소 조회 - placeName만 입력되는 경우")
	void findAllPlaceWithPlaceName() {

		String placeName = "test";
		PlaceType placeType = null;
		Pageable pageable = PageRequest.of(0, 10);

		// 이 테스트에서 PlaceGetAllResponseDto는 테스트 대상이 아니므로 mock 객체로 만들어 사용
		PlaceGetAllResponseDto dto = mock(PlaceGetAllResponseDto.class);
		Page<PlaceGetAllResponseDto> page = new PageImpl<>(List.of(dto));

		when(placeRepository.findAllByPlaceNameContaining(placeName, pageable)).thenReturn(page);

		Page<PlaceGetAllResponseDto> findAllPlace = placeServiceImpl.findAllPlace(placeName, placeType, pageable);

		assertAll("placeName만 있을 경우 조회 로직 검증",
			() -> assertEquals(1, findAllPlace.getContent().size()),
			() -> verify(placeRepository).findAllByPlaceNameContaining(placeName, pageable)
		);
	}

	@Test
	@DisplayName("전체 장소 조회 - placeType만 입력되는 경우")
	void findAllPlaceWithPlaceType() {

		String placeName = null;
		PlaceType placeType = PlaceType.ACCOMMODATION;
		Pageable pageable = PageRequest.of(0, 10);

		// 이 테스트에서 PlaceGetAllResponseDto는 테스트 대상이 아니므로 mock 객체로 만들어 사용
		PlaceGetAllResponseDto dto = mock(PlaceGetAllResponseDto.class);
		Page<PlaceGetAllResponseDto> page = new PageImpl<>(List.of(dto));

		when(placeRepository.findAllByPlaceType(placeType, pageable)).thenReturn(page);

		Page<PlaceGetAllResponseDto> findAllPlace = placeServiceImpl.findAllPlace(placeName, placeType, pageable);

		assertAll("파람이 모두 있을 경우 조회 로직 검증",
			() -> assertEquals(1, findAllPlace.getContent().size()),
			() -> verify(placeRepository).findAllByPlaceType(placeType, pageable)
		);
	}

	@Test
	@DisplayName("전체 장소 조회 - 파람이 모두 입력되지 않는 경우")
	void findAllPlace() {

		String placeName = null;
		PlaceType placeType = null;
		Pageable pageable = PageRequest.of(0, 10);

		// 이 테스트에서 PlaceGetAllResponseDto는 테스트 대상이 아니므로 mock 객체로 만들어 사용
		PlaceGetAllResponseDto dto = mock(PlaceGetAllResponseDto.class);
		Page<PlaceGetAllResponseDto> page = new PageImpl<>(List.of(dto));

		when(placeRepository.findAllPlace(pageable)).thenReturn(page);

		Page<PlaceGetAllResponseDto> findAllPlace = placeServiceImpl.findAllPlace(placeName, placeType, pageable);

		assertAll("파람이 모두 있을 경우 조회 로직 검증",
			() -> assertEquals(1, findAllPlace.getContent().size()),
			() -> verify(placeRepository).findAllPlace(pageable)
		);
	}

	@Test
	@DisplayName("특정 장소 조회")
	void findByPlaceId() {

		Place place = Place.builder()
			.placeName("testName")
			.placeType(PlaceType.ACCOMMODATION)
			.build();

		ReflectionTestUtils.setField(place, "id", 1L);

		PlaceReview placeReview1 = PlaceReview.builder()
			.place(place)
			.user(mock(User.class))
			.content("testContent1")
			.ratio(BigDecimal.ZERO)
			.build();

		PlaceReview placeReview2 = PlaceReview.builder()
			.place(place)
			.user(mock(User.class))
			.content("testContent2")
			.ratio(BigDecimal.ZERO)
			.build();

		ReflectionTestUtils.setField(placeReview1, "id", 1L);
		ReflectionTestUtils.setField(placeReview2, "id", 2L);
		ReflectionTestUtils.setField(placeReview1, "deletedAt", null);
		ReflectionTestUtils.setField(placeReview2, "deletedAt", LocalDateTime.now());

		ReflectionTestUtils.setField(place, "placeReviewList", List.of(placeReview1, placeReview2));

		when(placeRepository.findWithReviewListById(1L)).thenReturn(Optional.of(place));

		PlaceGetResponseDto dto = placeServiceImpl.findByPlaceId(1L);

		assertAll("특정 장소 조회 로직 검증",
			() -> assertEquals(1, dto.getPlaceReviewList().size()),
			() -> assertEquals("testName", dto.getPlaceName()),
			() -> assertEquals("testContent1", dto.getPlaceReviewList().get(0).getContent())
		);
	}

	@Test
	@DisplayName("장소 수정")
	void updatePlace() {

		Place place = Place.builder()
			.placeName("testName")
			.placeType(PlaceType.ACCOMMODATION)
			.latitude(BigDecimal.ONE)
			.longitude(BigDecimal.ONE)
			.build();

		ReflectionTestUtils.setField(place, "id", 1L);

		PlaceUpdateRequestDto dto = new PlaceUpdateRequestDto(
			"updateTestName",
			null,
			PlaceType.CAFE,
			BigDecimal.ZERO,
			BigDecimal.ZERO);

		when(placeRepository.findById(1L)).thenReturn(Optional.of(place));

		PlaceUpdateResponseDto responseDto = placeServiceImpl.updatePlace(1L, dto);

		assertAll("장소 수정 로직 검증",
			() -> assertEquals("updateTestName", responseDto.getPlaceName()),
			() -> assertEquals(PlaceType.CAFE, responseDto.getPlaceType()),
			() -> assertEquals(BigDecimal.ZERO, responseDto.getLatitude()),
			() -> assertEquals(BigDecimal.ZERO, responseDto.getLongitude())
		);
	}

	@Test
	@DisplayName("장소 삭제")
	void deletePlace() {

		Place place = Place.builder()
			.placeName("testName")
			.build();

		ReflectionTestUtils.setField(place, "id", 1L);

		PlaceStatusChangeRequestDto dto = new PlaceStatusChangeRequestDto(PlaceStatus.DELETED);

		when(placeRepository.findById(1L)).thenReturn(Optional.of(place));

		placeServiceImpl.deletePlace(1L, dto);

		assertAll("장소 삭제 로직 검증",
			() -> assertEquals(PlaceStatus.DELETED, place.getPlaceStatus()),
			() -> assertNotNull(place.getDeletedAt())
		);
	}

	@Test
	@DisplayName("장소 복구")
	void restorePlace() {

		Place place = Place.builder()
			.placeName("testName")
			.build();

		ReflectionTestUtils.setField(place, "id", 1L);

		PlaceStatusChangeRequestDto dto = new PlaceStatusChangeRequestDto(PlaceStatus.ACTIVE);

		when(placeRepository.findById(1L)).thenReturn(Optional.of(place));

		placeServiceImpl.restorePlace(1L, dto);

		assertAll("장소 삭제 로직 검증",
			() -> assertEquals(PlaceStatus.ACTIVE, place.getPlaceStatus()),
			() -> assertNull(place.getDeletedAt())
		);
	}
}