package com.study.petory.domain.calendar.dto.response;

import java.util.List;

import com.study.petory.common.util.CustomDateUtil;
import com.study.petory.domain.calendar.entity.Event;

import lombok.Getter;

@Getter
public class EventUpdateResponseDto {

	private final Long id;

	private final String title;

	private final String startDate;

	private final String endDate;

	private final Boolean isAllDay;

	private final List<String> recurrence;

	private final String description;

	private final String color;

	private EventUpdateResponseDto(Long id, String title, String startDate, String endDate,
		Boolean isAllDay, List<String> recurrence, String description, String color) {
		this.id = id;
		this.title = title;
		this.startDate = startDate;
		this.endDate = endDate;
		this.isAllDay = isAllDay;
		this.recurrence = recurrence;
		this.description = description;
		this.color = color;
	}

	public static EventUpdateResponseDto from(Event event) {
		return new EventUpdateResponseDto(
			event.getId(),
			event.toString(),
			CustomDateUtil.toISOString(event.getStartDate(), event.getTimeZone()),
			CustomDateUtil.toISOString(event.getEndDate(), event.getTimeZone()),
			event.getIsAllDay(),
			event.getRecurrence(),
			event.getDescription(),
			event.getColor()
		);
	}
}
