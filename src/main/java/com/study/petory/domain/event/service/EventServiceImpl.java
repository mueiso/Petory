package com.study.petory.domain.event.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.study.petory.common.exception.CustomException;
import com.study.petory.common.exception.enums.ErrorCode;
import com.study.petory.common.util.CustomDateUtil;
import com.study.petory.domain.event.dto.request.EventCreateRequestDto;
import com.study.petory.domain.event.dto.request.EventUpdateRequestDto;
import com.study.petory.domain.event.dto.response.EventCreateResponseDto;
import com.study.petory.domain.event.dto.response.EventGetOneResponseDto;
import com.study.petory.domain.event.dto.response.EventInstanceGetResponseDto;
import com.study.petory.domain.event.dto.response.EventUpdateResponseDto;
import com.study.petory.domain.event.entity.Event;
import com.study.petory.domain.event.entity.Recurrence;
import com.study.petory.domain.event.repository.EventRepository;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

	private final EventRepository eventRepository;
	private final UserService userService;
	private final Recurrence recurrence;

	// 일정 조회
	@Override
	public Event findEventById(Long eventId) {
		return eventRepository.findById(eventId)
			.orElseThrow(() -> new CustomException(ErrorCode.CALENDER_NOT_FOUND));
	}

	// 이벤트 생성
	@Override
	@Transactional
	public EventCreateResponseDto saveEvent(Long userId, EventCreateRequestDto request) {
		User user = userService.findUserById(userId);

		Event event = Event.builder()
			.user(user)
			.title(request.getTitle())
			.startDate(CustomDateUtil.stringToLocalDateTime(request.getStartDate()))
			.endDate(CustomDateUtil.stringToLocalDateTime(request.getEndDate()))
			.timeZone(request.getTimeZone())
			.isAllDay(Optional.ofNullable(request.getIsAllDay()).orElse(true))
			.rrule(recurrence.getRecurrence("RRULE", request.getRecurrence()))
			.recurrenceEnd(recurrence.getRecurrenceEnd(request.getRecurrence()))
			.rDate(recurrence.getRecurrence("RDATE", request.getRecurrence()))
			.exDate(recurrence.getRecurrence("EXDATE", request.getRecurrence()))
			.description(request.getDescription())
			.color(request.getColor())
			.build();

		Event savedEvent = eventRepository.save(event);
		return EventCreateResponseDto.from(savedEvent);
	}

	// 일정 범위 조회
	@Override
	@Transactional
	public List<EventInstanceGetResponseDto> findEvents(Long userId, String start, String end) {
		LocalDateTime startDate = CustomDateUtil.stringToLocalDateTime(start);
		LocalDateTime endDate = CustomDateUtil.stringToLocalDateTime(end);

		List<Event> eventList = eventRepository.findEventListStart(userId, startDate, endDate);

		return eventList.stream()
			.flatMap(event -> {
				if (event.getRrule() == null) {
					return Stream.of(EventInstanceGetResponseDto.from(event, null));
				} else {
					List<LocalDateTime> instanceTimeList = recurrence.getInstanceStartTimeList(event, startDate, endDate);
					return instanceTimeList.stream()
						.map(instanceTime -> EventInstanceGetResponseDto.from(event, instanceTime));
				}
			})
			.sorted(Comparator.comparing(EventInstanceGetResponseDto::getStartDate))
			.collect(Collectors.toList());
	}

	// 일정 단일 조회
	@Override
	@Transactional
	public EventGetOneResponseDto findOneEvent(Long eventId) {
		Event event = findEventById(eventId);
		return EventGetOneResponseDto.from(event);
	}

	// 일정 수정
	@Override
	@Transactional
	public EventUpdateResponseDto updateEvent(Long userId, Long eventId, EventUpdateRequestDto request) {
		Event event = findEventById(eventId);
		validUser(userId, event);

		event.updateEvent(
			request.getTitle(),
			CustomDateUtil.stringToLocalDateTime(request.getStartDate()),
			CustomDateUtil.stringToLocalDateTime(request.getEndDate()),
			request.getTimeZone(),
			request.getIsAllDay(),
			recurrence.getRecurrence("RRULE", request.getRecurrence()),
			recurrence.getRecurrenceEnd(request.getRecurrence()),
			recurrence.getRecurrence("RDATE", request.getRecurrence()),
			recurrence.getRecurrence("EXDATE", request.getRecurrence()),
			request.getDescription(),
			request.getColor()
		);

		return EventUpdateResponseDto.from(event);
	}

	// 일정 삭제
	@Override
	@Transactional
	public void deleteEvent(Long userId, Long eventId) {
		Event event = findEventById(eventId);
		validUser(userId, event);
		eventRepository.deleteById(eventId);
	}

	// 작성자 검증
	public void validUser(Long userId, Event event) {
		if (!event.isEqualUser(userId)) {
			throw new CustomException(ErrorCode.ONLY_AUTHOR_CAN_EDIT);
		}
	}
}
