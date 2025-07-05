package com.study.petory.domain.place.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
import com.study.petory.domain.place.dto.request.PlaceReportCancelRequestDto;
import com.study.petory.domain.place.dto.request.PlaceReportRequestDto;
import com.study.petory.domain.place.entity.Place;
import com.study.petory.domain.place.entity.PlaceReport;
import com.study.petory.domain.place.entity.PlaceStatus;
import com.study.petory.domain.place.repository.PlaceReportRepository;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.service.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
public class PlaceReportServiceTest {

	@Mock
	private PlaceReportRepository placeReportRepository;

	@Mock
	private PlaceServiceImpl placeServiceImpl;

	@Mock
	private UserServiceImpl userServiceImpl;

	@InjectMocks
	private PlaceReportService placeReportService;

	@Test
	@DisplayName("장소 신고 - 장소가 비활성화 상태인 경우")
	void reportPlaceIsDeletedAtNotNull() {

		Place place = new Place();
		place.deactivateEntity();

		ReflectionTestUtils.setField(place, "id", 1L);

		PlaceReportRequestDto requestDto = new PlaceReportRequestDto("testContent");

		when(placeServiceImpl.findPlaceByPlaceId(1L)).thenReturn(place);

		CustomException customException = assertThrows(
			CustomException.class,
			() -> placeReportService.reportPlace(1L, 1L, requestDto)
		);

		assertAll("장소가 비활성화 상태일 때 장소 신고 로직 검증",
			() -> assertEquals(ErrorCode.ALREADY_INACTIVE_PLACE, customException.getErrorCode())
		);
	}

	@Test
	@DisplayName("장소 신고 - 장소가 비활성화 상태인 경우 2")
	void reportPlaceIsPlaceStatusInActive() {
		Place place = new Place();

		ReflectionTestUtils.setField(place, "id", 1L);
		ReflectionTestUtils.setField(place, "placeStatus", PlaceStatus.INACTIVE);

		PlaceReportRequestDto requestDto = new PlaceReportRequestDto("testContent");

		when(placeServiceImpl.findPlaceByPlaceId(1L)).thenReturn(place);

		CustomException customException = assertThrows(
			CustomException.class,
			() -> placeReportService.reportPlace(1L, 1L, requestDto)
		);

		assertAll("장소가 비활성화 상태일 때 장소 신고 로직 검증",
			() -> assertEquals(ErrorCode.ALREADY_INACTIVE_PLACE, customException.getErrorCode())
		);
	}

	@Test
	@DisplayName("장소 신고 - 이미 신고한 경우")
	void AlreadyReportPlace() {
		Place place = new Place();

		ReflectionTestUtils.setField(place, "id", 1L);

		User user = new User();

		ReflectionTestUtils.setField(user, "id", 1L);

		PlaceReportRequestDto requestDto = new PlaceReportRequestDto("testContent");

		when(placeServiceImpl.findPlaceByPlaceId(1L)).thenReturn(place);
		when(userServiceImpl.findUserById(1L)).thenReturn(user);
		when(placeReportRepository.existsByUserIdAndPlaceId(1L, 1L)).thenReturn(true);

		CustomException customException = assertThrows(
			CustomException.class,
			() -> placeReportService.reportPlace(1L, 1L, requestDto)
		);

		assertAll("이미 신고된 장소인 경우 신고 로직 검증",
			() -> assertEquals(ErrorCode.ALREADY_REPORT_PLACE, customException.getErrorCode())
		);
	}

	@Test
	@DisplayName("장소 신고 - 장소 신고가 9개 미만인 경우")
	void reportPlaceCountPlaceReportIs9() {
		Place place = new Place();

		ReflectionTestUtils.setField(place, "id", 1L);
		ReflectionTestUtils.setField(place, "id", 1L);

		User user = new User();

		ReflectionTestUtils.setField(user, "id", 1L);

		PlaceReportRequestDto requestDto = new PlaceReportRequestDto("testContent");

		when(placeServiceImpl.findPlaceByPlaceId(1L)).thenReturn(place);
		when(userServiceImpl.findUserById(1L)).thenReturn(user);
		when(placeReportRepository.existsByUserIdAndPlaceId(1L, 1L)).thenReturn(false);

		when(placeReportRepository.save(any(PlaceReport.class))).thenAnswer(invocationOnMock -> {
			PlaceReport savedPlaceReport = invocationOnMock.getArgument(0);
			ReflectionTestUtils.setField(savedPlaceReport, "id", 1L);
			return savedPlaceReport;
		});
		when(placeReportRepository.countPlaceReportByPlaceAndIsValidAndReportResetAt(place)).thenReturn(9L);

		placeReportService.reportPlace(1L, 1L, requestDto);

		assertAll("장소 신고가 9개 미만인 경우 장소 신고 로직 검증",
			() -> assertEquals(place.getPlaceStatus(), PlaceStatus.ACTIVE)
		);
	}

	@Test
	@DisplayName("장소 신고 - 장소 신고가 10개 이상인 경우")
	void reportPlaceCountPlaceReportIs10() {
		Place place = new Place();

		ReflectionTestUtils.setField(place, "id", 1L);
		ReflectionTestUtils.setField(place, "id", 1L);

		User user = new User();

		ReflectionTestUtils.setField(user, "id", 1L);

		PlaceReportRequestDto requestDto = new PlaceReportRequestDto("testContent");

		when(placeServiceImpl.findPlaceByPlaceId(1L)).thenReturn(place);
		when(userServiceImpl.findUserById(1L)).thenReturn(user);
		when(placeReportRepository.existsByUserIdAndPlaceId(1L, 1L)).thenReturn(false);

		when(placeReportRepository.save(any(PlaceReport.class))).thenAnswer(invocationOnMock -> {
			PlaceReport savedPlaceReport = invocationOnMock.getArgument(0);
			ReflectionTestUtils.setField(savedPlaceReport, "id", 1L);
			return savedPlaceReport;
		});
		when(placeReportRepository.countPlaceReportByPlaceAndIsValidAndReportResetAt(place)).thenReturn(10L);

		placeReportService.reportPlace(1L, 1L, requestDto);

		assertAll("장소 신고가 10개 이상인 경우 장소 신고 로직 검증",
			() -> assertNotNull(place.getDeletedAt()),
			() -> assertEquals(place.getPlaceStatus(), PlaceStatus.INACTIVE)
		);
	}

	@Test
	@DisplayName("장소 신고 취소 - 장소 불일치의 경우")
	void cancelReportPlaceInconsistentPlace() {
		Place place1 = new Place();
		Place place2 = new Place();

		ReflectionTestUtils.setField(place1, "id", 1L);
		ReflectionTestUtils.setField(place2, "id", 2L);

		User user = new User();

		ReflectionTestUtils.setField(user, "id", 1L);

		PlaceReport placeReport = PlaceReport.builder()
			.user(user)
			.place(place1)
			.content("test")
			.build();

		ReflectionTestUtils.setField(placeReport, "id", 1L);

		when(placeReportRepository.findById(1L)).thenReturn(Optional.of(placeReport));

		CustomException customException = assertThrows(
			CustomException.class,
			() -> placeReportService.cancelReportPlace(1L, 2L, 1L, new PlaceReportCancelRequestDto("test"))
		);

		assertAll("장소가 비활성화 상태일 때 장소 신고 로직 검증",
			() -> assertEquals(ErrorCode.INCONSISTENT_PLACE, customException.getErrorCode())
		);
	}

	@Test
	@DisplayName("장소 신고 취소 - 이미 취소된 경우")
	void cancelReportPlaceAlreadyInvalidReport() {
		Place place1 = new Place();

		ReflectionTestUtils.setField(place1, "id", 1L);

		User user = new User();

		ReflectionTestUtils.setField(user, "id", 1L);

		PlaceReport placeReport = PlaceReport.builder()
			.user(user)
			.place(place1)
			.content("test")
			.build();

		ReflectionTestUtils.setField(placeReport, "id", 1L);
		ReflectionTestUtils.setField(placeReport, "isValid", false);

		when(placeReportRepository.findById(1L)).thenReturn(Optional.of(placeReport));

		CustomException customException = assertThrows(
			CustomException.class,
			() -> placeReportService.cancelReportPlace(1L, 1L, 1L, new PlaceReportCancelRequestDto("test"))
		);

		assertAll("장소 신고가 이미 취소된 경우 장소 신고 로직 검증",
			() -> assertEquals(ErrorCode.ALREADY_INVALID_REPORT, customException.getErrorCode())
		);
	}

	@Test
	@DisplayName("장소 신고 취소")
	void cancelPlaceReport() {
		Place place1 = new Place();

		ReflectionTestUtils.setField(place1, "id", 1L);

		User user = new User();

		ReflectionTestUtils.setField(user, "id", 1L);

		PlaceReport placeReport = PlaceReport.builder()
			.user(user)
			.place(place1)
			.content("test")
			.build();

		ReflectionTestUtils.setField(placeReport, "id", 1L);

		PlaceReportCancelRequestDto requestDto = new PlaceReportCancelRequestDto("test");

		when(placeReportRepository.findById(1L)).thenReturn(Optional.of(placeReport));

		placeReportService.cancelReportPlace(1L, 1L, 1L, requestDto);

		assertAll("장소 신고 취소 로직 검증",
			() -> assertEquals(placeReport.isValid(), false),
			() -> assertNotNull(placeReport.getDeletedAt())
		);
	}
}
