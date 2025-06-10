package com.study.petory.domain.place.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import com.study.petory.domain.place.dto.request.PlaceCreateRequestDto;
import com.study.petory.domain.place.dto.response.PlaceCreateResponseDto;
import com.study.petory.domain.place.entity.Place;
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
	void findAllPlace() {
	}

	@Test
	void findByPlaceId() {
	}

	@Test
	void updatePlace() {
	}

	@Test
	void deletePlace() {
	}

	@Test
	void restorePlace() {
	}

	@Test
	void findPlaceByPlaceId() {
	}
}