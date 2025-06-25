package com.study.petory.domain.calendar.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.study.petory.domain.calendar.entity.Event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class EventGetListResponseDto {

	private final Long id;

	private final String title;

	private final LocalDateTime startDate;

	private final LocalDateTime endDate;

	private final String timeZone;

	private final boolean isAllDay;

	private final List<String> recurrence;

	private final String color;
}
