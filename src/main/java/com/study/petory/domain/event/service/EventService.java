package com.study.petory.domain.event.service;

import java.util.List;

import com.study.petory.domain.event.dto.request.EventCreateRequestDto;
import com.study.petory.domain.event.dto.request.EventUpdateRequestDto;
import com.study.petory.domain.event.dto.response.EventCreateResponseDto;
import com.study.petory.domain.event.dto.response.EventInstanceGetResponseDto;
import com.study.petory.domain.event.dto.response.EventGetOneResponseDto;
import com.study.petory.domain.event.dto.response.EventUpdateResponseDto;
import com.study.petory.domain.event.entity.Event;

public interface EventService {

	Event findEventById(Long eventId);

	void validUser(Long userId, Event event);

	EventCreateResponseDto saveEvent(Long userId, EventCreateRequestDto request);

	EventGetOneResponseDto findOneEvent(Long eventId);

	List<EventInstanceGetResponseDto> findEvents(Long userId, String start, String end);

	EventUpdateResponseDto updateEvent(Long userId, Long eventId, EventUpdateRequestDto request);

	void deleteEvent(Long userId, Long eventId);
}
