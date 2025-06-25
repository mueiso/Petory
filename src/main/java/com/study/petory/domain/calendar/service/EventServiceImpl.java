package com.study.petory.domain.calendar.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.study.petory.common.exception.CustomException;
import com.study.petory.common.exception.enums.ErrorCode;
import com.study.petory.common.util.DateUtil;
import com.study.petory.domain.calendar.dto.request.EventCreateRequestDto;
import com.study.petory.domain.calendar.dto.request.EventUpdateRequestDto;
import com.study.petory.domain.calendar.dto.response.EventCreateResponseDto;
import com.study.petory.domain.calendar.dto.response.EventGetListResponseDto;
import com.study.petory.domain.calendar.dto.response.EventGetOneResponseDto;
import com.study.petory.domain.calendar.dto.response.EventUpdateResponseDto;
import com.study.petory.domain.calendar.entity.Event;
import com.study.petory.domain.calendar.repository.EventRepository;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

	private final EventRepository eventRepository;
	private final UserService userService;

	// @Override
	// public Event findEventById(Long eventId) {
	// 	return eventRepository.findById(eventId)
	// 		.orElseThrow(() -> new CustomException(ErrorCode.CALENDER_NOT_FOUND));
	// }
	//
	// // 이벤트 생성
	// @Override
	// @Transactional
	// public EventCreateResponseDto saveEvent(Long userId, EventCreateRequestDto request) {
	// 	User user = userService.getUserById(userId);
	//
	// 	Event event = Event.builder()
	// 		.user(user)
	// 		.title(request.getTitle())
	// 		.startDate(request.getStartDate())
	// 		.endDate(Optional.ofNullable(request.getEndDate()).orElse(null))
	// 		.timeZone(request.getTimeZone())
	// 		.isAllDay(Optional.ofNullable(request.getIsAllDay()).orElse(true))
	// 		.recurrence(Optional.ofNullable(toStringRecurrence(request.getRecurrence())).orElse(null))
	// 		.description(Optional.ofNullable(request.getDescription()).orElse(null))
	// 		.color(Optional.ofNullable(request.getColor()).orElse(null))
	// 		.build();
	//
	// 	Event savedEvent = eventRepository.save(event);
	// 	return new EventCreateResponseDto(
	// 		savedEvent.getId(),
	// 		savedEvent.getTitle(),
	// 		savedEvent.getStartDate(),
	// 		Optional.ofNullable(savedEvent.getEndDate()).orElse(null),
	// 		savedEvent.getTimeZone(),
	// 		savedEvent.getIsAllDay(),
	// 		Optional.ofNullable(toStringRecurrence(savedEvent.getRecurrence())).orElse(null),
	// 		Optional.ofNullable(savedEvent.getDescription()).orElse(null),
	// 		Optional.ofNullable(savedEvent.getColor()).orElse(null)
	// 	);
	// }
	//
	// //
	// @Override
	// public EventGetOneResponseDto findOneEvent(Long eventId) {
	// 	Event event = findEventById(eventId);
	// 	return new EventGetOneResponseDto(
	// 		event.getId(),
	// 		event.getTitle(),
	// 		event.getStartDate(),
	// 		Optional.ofNullable(event.getEndDate()).orElseThrow(null),
	// 		Optional.ofNullable(event.getTimeZone()).orElse(null),
	// 		event.getIsAllDay(),
	// 		Optional.ofNullable(toStringRecurrence(event.getRecurrence())).orElse(null),
	// 		Optional.ofNullable(event.getDescription()).orElseThrow(null),
	// 		Optional.ofNullable(event.getColor()).orElseThrow(null)
	// 	);
	// }
	//
	// @Override
	// @Transactional
	// public List<EventGetListResponseDto> findEvents(Long userId, String start, String end) {
	// 	LocalDateTime startDate = DateUtil.localDateTimeToString(start);
	// 	LocalDateTime endDate = DateUtil.localDateTimeToString(end);
	// 	List<Event> eventList = eventRepository.findEventList(userId, startDate, endDate);
	// 	List<EventGetListResponseDto> a = eventList.stream().map(event -> new EventGetListResponseDto(
	// 		event.getId(),
	// 		event.getTitle(),
	// 		event.getStartDate(),
	// 		Optional.ofNullable(event.getEndDate()).orElse(null),
	// 		Optional.ofNullable(event.getTimeZone()).orElse(null),
	// 		Optional.ofNullable(event.getIsAllDay()).orElse(true),
	// 		Optional.ofNullable(toStringRecurrence(event.getRecurrence())).orElse(null),
	// 		Optional.ofNullable(event.getColor()).orElse(null)
	// 	)).toList();
	//
	// 	return a;
	// }
	//
	// @Override
	// @Transactional
	// public EventUpdateResponseDto updateEvent(Long userId, Long eventId, EventUpdateRequestDto request) {
	// 	Event event = findEventById(eventId);
	// 	validUser(userId, event);
	//
	// 	String recurrence = null;
	// 	if (event.getRecurrence() != null) {
	// 		recurrence = toStringRecurrence(request.getRecurrence());
	// 	}
	//
	// 	event.updateEvent(
	// 		request.getTitle(),
	// 		request.getStartDate(),
	// 		Optional.ofNullable(request.getEndDate()).orElse(null),
	// 		request.getTimeZone(),
	// 		Optional.ofNullable(request.getIsAllDay()).orElse(true),
	// 		recurrence,
	// 		Optional.ofNullable(request.getDescription()).orElseThrow(null),
	// 		Optional.ofNullable(request.getColor()).orElse(null)
	// 	);
	// 	return new EventUpdateResponseDto(
	// 		eventId,
	// 		event.getTitle(),
	// 		event.getStartDate(),
	// 		event.getEndDate(),
	// 		event.getTimeZone(),
	// 		event.getIsAllDay(),
	// 		Optional.ofNullable(toStringRecurrence(event.getRecurrence())).orElse(null),
	// 		event.getDescription(),
	// 		event.getColor()
	// 	);
	// }
	//
	// @Override
	// @Transactional
	// public void deleteEvent(Long userId, Long eventId) {
	// 	Event event = findEventById(eventId);
	// 	validUser(userId, event);
	// 	eventRepository.deleteById(eventId);
	// }
	//
	// public void validUser(Long userId, Event event) {
	// 	if (!event.isEqualUser(userId)) {
	// 		throw new CustomException(ErrorCode.ONLY_AUTHOR_CAN_EDIT);
	// 	}
	// }
	//
	// public String toStringRecurrence(List<String> recurrence) {
	// 	if (recurrence == null) {
	// 		return null;
	// 	}
	// 	return String.join(";;", recurrence);
	// }
	//
	// public List<String> toStringRecurrence(String recurrence) {
	// 	if (recurrence == null || recurrence.isBlank()) {
	// 		return new ArrayList<>();
	// 	}
	// 	String[] responseList = recurrence.split(";;");
	// 	return new ArrayList<>(Arrays.asList(responseList));
	// }
}
