package com.study.petory.domain.event.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.study.petory.common.exception.CustomException;
import com.study.petory.domain.event.dto.request.EventCreateRequestDto;
import com.study.petory.domain.event.dto.request.EventUpdateRequestDto;
import com.study.petory.domain.event.dto.response.EventCreateResponseDto;
import com.study.petory.domain.event.dto.response.EventGetOneResponseDto;
import com.study.petory.domain.event.dto.response.EventInstanceGetResponseDto;
import com.study.petory.domain.event.dto.response.EventUpdateResponseDto;
import com.study.petory.domain.event.entity.Event;
import com.study.petory.domain.event.entity.EventColor;
import com.study.petory.domain.user.entity.Role;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.entity.UserPrivateInfo;
import com.study.petory.domain.user.entity.UserRole;
import com.study.petory.domain.user.repository.UserRepository;

@SpringBootTest
@Transactional
@Rollback
@ActiveProfiles("test")
public class EventServiceTest {

	@Autowired
	private EventService eventService;

	@Autowired
	private UserRepository userRepository;

	private Long userId;

	private final UserPrivateInfo userInfo = new UserPrivateInfo(
		"1L",
		"홍길동",
		"000-0000-0000"
	);
	;

	private final List<UserRole> userRole = new ArrayList<>(
		List.of(new UserRole(Role.USER))
	);

	private final User user = User.builder()
		.email("test@email.com")
		.nickname("길동이")
		.userPrivateInfo(userInfo)
		.userRole(userRole)
		.build();

	@BeforeEach
	public void setUser() {
		userRepository.save(user);
		userId = user.getId();
	}

	@Test
	@DisplayName("일정을 저장한다.")
	public void testSaveEvent() {
		// given
		EventCreateRequestDto request = new EventCreateRequestDto(
			"일정 제목",
			"2025-07-18T09:00:00",
			"2025-07-18T21:00:00",
			"Asia/Seoul",
			false,
			List.of("RRULE:FREQ=DAILY;INTERVAL=2;COUNT=3"),
			null,
			EventColor.GRAPHITE
		);

		// when
		EventCreateResponseDto response = eventService.saveEvent(userId, request);

		// then
		assertThat(response.getTitle()).isEqualTo("일정 제목");
		assertThat(response.getStartDate()).isEqualTo("2025-07-18T09:00:00+09:00");
		assertThat(response.getEndDate()).isEqualTo("2025-07-18T21:00:00+09:00");
		assertThat(response.getIsAllDay()).isFalse();
		assertThat(response.getRecurrence()).isEqualTo(List.of("RRULE:FREQ=DAILY;INTERVAL=2;COUNT=3"));
		assertThat(response.getDescription()).isNull();
		assertThat(response.getColor()).isEqualTo(EventColor.GRAPHITE);
	}

	@Test
	@DisplayName("기간으로 일정을 조회한다.")
	public void testFindEvents() {
		// given
		String start = "2025-06-29";
		String end = "2025-08-09";

		EventCreateRequestDto request1 = new EventCreateRequestDto(
			"일정 제목",
			"2025-06-17T00:00:00",
			"2025-06-17T00:00:00",
			"Asia/Seoul",
			true,
			List.of("RRULE:FREQ=WEEKLY;BYDAY=TU;INTERVAL=2;COUNT=10", "EXDATE:20250715T000000Z"),
			null,
			EventColor.GRAPHITE
		);
		EventCreateRequestDto request2 = new EventCreateRequestDto(
			"일정 제목",
			"2025-07-11T00:00:00",
			"2025-07-13T00:00:00",
			"Asia/Seoul",
			true,
			List.of(),
			null,
			EventColor.TOMATO
		);

		eventService.saveEvent(userId, request1);
		eventService.saveEvent(userId, request2);

		// when
		List<EventInstanceGetResponseDto> response = eventService.findEvents(userId, start, end);

		// then
		assertThat(response.size()).isEqualTo(3);
		assertThat(response.get(0).getStartDate()).isEqualTo("2025-07-01T00:00:00+09:00");
		assertThat(response.get(0).getColor()).isEqualTo(EventColor.GRAPHITE);

		assertThat(response.get(1).getStartDate()).isEqualTo("2025-07-11T00:00:00+09:00");
		// 조회 시 isAllDay == ture라면, endDate.plusDay(1)
		assertThat(response.get(1).getEndDate()).isEqualTo("2025-07-14T00:00:00+09:00");
		assertThat(response.get(1).getColor()).isEqualTo(EventColor.TOMATO);

		assertThat(response.get(2).getStartDate()).isEqualTo("2025-07-29T00:00:00+09:00");
		assertThat(response.get(2).getColor()).isEqualTo(EventColor.GRAPHITE);
	}

	@Test
	@DisplayName("단일 조회")
	public void testFindOneEvent() {
		// given
		EventCreateRequestDto request = new EventCreateRequestDto(
			"일정 제목",
			"2025-06-17T00:00:00",
			"2025-06-17T00:00:00",
			"Asia/Seoul",
			true,
			List.of("RRULE:FREQ=WEEKLY;BYDAY=TU;INTERVAL=2;COUNT=10", "EXDATE:20250715T000000Z"),
			null,
			EventColor.GRAPHITE
		);

		EventCreateResponseDto event = eventService.saveEvent(userId, request);

		// when
		EventGetOneResponseDto response = eventService.findOneEvent(event.getId());

		// then
		assertThat(response.getTitle()).isEqualTo("일정 제목");
		assertThat(response.getStartDate()).isEqualTo("2025-06-17T00:00:00+09:00");
		// 조회 시 isAllDay == ture라면, endDate.plusDay(1)
		assertThat(response.getEndDate()).isEqualTo("2025-06-18T00:00:00+09:00");
		assertThat(response.isAllDay()).isEqualTo(true);
		assertThat(response.getRecurrence()).isEqualTo(
			List.of("RRULE:FREQ=WEEKLY;BYDAY=TU;INTERVAL=2;COUNT=10", "EXDATE:20250715T000000Z"));
		assertThat(response.getDescription()).isEqualTo(null);
		assertThat(response.getColor()).isEqualTo(EventColor.GRAPHITE);
	}

	@Test
	@DisplayName("일정을 수정한다")
	public void testUpdateEvent() {
		// given
		EventCreateRequestDto request = new EventCreateRequestDto(
			"일정 제목",
			"2025-06-17T00:00:00",
			"2025-06-17T00:00:00",
			"Asia/Seoul",
			true,
			List.of("RRULE:FREQ=WEEKLY;BYDAY=TU;INTERVAL=2;COUNT=10", "EXDATE:20250715T000000Z"),
			null,
			EventColor.GRAPHITE
		);
		EventUpdateRequestDto updateRequest = new EventUpdateRequestDto(
			"수정된 제목",
			"2025-07-01T09:00:00",
			"2025-07-01T14:00:00",
			"Asia/Seoul",
			false,
			null,
			"수정된 메모",
			EventColor.SAGE
		);

		EventCreateResponseDto event = eventService.saveEvent(userId, request);

		// when
		EventUpdateResponseDto updateEvent = eventService.updateEvent(userId, event.getId(), updateRequest);

		// then
		assertThat(updateEvent.getTitle()).isEqualTo("수정된 제목");
		assertThat(updateEvent.getStartDate()).isEqualTo("2025-07-01T09:00:00+09:00");
		// 조회 시 isAllDay == ture라면, endDate.plusDay(1)
		assertThat(updateEvent.getEndDate()).isEqualTo("2025-07-01T14:00:00+09:00");
		assertThat(updateEvent.getIsAllDay()).isEqualTo(false);
		assertThat(updateEvent.getRecurrence()).isEqualTo(List.of());
		assertThat(updateEvent.getDescription()).isEqualTo("수정된 메모");
		assertThat(updateEvent.getColor()).isEqualTo(EventColor.SAGE);
	}

	@Test
	@DisplayName("일정을 삭제한다")
	public void testDeleteEvent() {
		// given
		EventCreateRequestDto request = new EventCreateRequestDto(
			"일정 제목",
			"2025-06-17T00:00:00",
			"2025-06-17T00:00:00",
			"Asia/Seoul",
			true,
			List.of("RRULE:FREQ=WEEKLY;BYDAY=TU;INTERVAL=2;COUNT=10", "EXDATE:20250715T000000Z"),
			null,
			EventColor.GRAPHITE
		);
		EventCreateResponseDto event = eventService.saveEvent(userId, request);

		// when
		eventService.deleteEvent(userId, event.getId());

		// then
		CustomException exception = assertThrows(CustomException.class,
			() -> eventService.findEventById(event.getId()));

		assertEquals("일정이 존재하지 않습니다.", exception.getMessage());
	}

	@Test
	@DisplayName("요청한 사용자가 일정의 작성자가 아니라면 예외 출력")
	public void testValidUser() {
		// given
		EventCreateRequestDto request = new EventCreateRequestDto(
			"일정 제목",
			"2025-07-18T00:00:00",
			"2025-07-18T00:00:00",
			"Asia/Seoul",
			true,
			List.of("RRULE:FREQ=DAILY;INTERVAL=2;COUNT=3"),
			null,
			EventColor.GRAPHITE
		);
		EventCreateResponseDto saveEvent = eventService.saveEvent(userId, request);
		Event event = eventService.findEventById(saveEvent.getId());
		Long otherUserId = userId + 1L;

		// when
		CustomException exception = assertThrows(CustomException.class,
			() -> eventService.validUser(otherUserId, event));

		// then
		assertEquals("작성자만 수정이 가능합니다.", exception.getMessage());
	}

	@Test
	@DisplayName("일정의 id로 일정을 조회한다")
	public void testFindEventById() {
		// given
		EventCreateRequestDto request = new EventCreateRequestDto(
			"일정 제목",
			"2025-07-18T00:00:00",
			"2025-07-18T00:00:00",
			"Asia/Seoul",
			true,
			List.of("RRULE:FREQ=DAILY;INTERVAL=2;COUNT=3", "RDATE:20250719T000000Z", "EXDATE:20250720T000000Z"),
			null,
			EventColor.GRAPHITE
		);
		EventCreateResponseDto saveEvent = eventService.saveEvent(userId, request);

		// when
		Event event = eventService.findEventById(saveEvent.getId());

		// then
		assertThat(event.getStartDate()).isEqualTo("2025-07-18T00:00:00");
		assertThat(event.getEndDate()).isEqualTo("2025-07-18T00:00:00");
		assertThat(event.getTimeZone()).isEqualTo("Asia/Seoul");
		assertThat(event.getIsAllDay()).isTrue();
		assertThat(event.getRrule()).isEqualTo("FREQ=DAILY;INTERVAL=2;COUNT=3");
		assertThat(event.getRDate()).isEqualTo("20250719T000000Z");
		assertThat(event.getExDate()).isEqualTo("20250720T000000Z");
		assertThat(event.getDescription()).isEqualTo(null);
		assertThat(event.getColor()).isEqualTo(EventColor.GRAPHITE);
	}
}
