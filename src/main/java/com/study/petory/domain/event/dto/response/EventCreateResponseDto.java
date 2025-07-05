package com.study.petory.domain.event.dto.response;

import java.util.List;

import com.study.petory.common.util.CustomDateUtil;
import com.study.petory.domain.event.entity.Event;
import com.study.petory.domain.event.entity.EventColor;

import lombok.Getter;

@Getter
public class EventCreateResponseDto {

	private final Long id;

	private final String title;

	private final String startDate;

	private final String endDate;

	private final Boolean isAllDay;

	private final List<String> recurrence;

	private final String description;

	private final EventColor color;

	private EventCreateResponseDto(Long id, String title, String startDate, String endDate,
		Boolean isAllDay, List<String> recurrence, String description, EventColor color) {
		this.id = id;
		this.title = title;
		this.startDate = startDate;
		this.endDate = endDate;
		this.isAllDay = isAllDay;
		this.recurrence = recurrence;
		this.description = description;
		this.color = color;
	}

	public static EventCreateResponseDto from(Event event) {
		return new EventCreateResponseDto(
			event.getId(),
			event.getTitle(),
			CustomDateUtil.toISOString(event.getStartDate(), event.getTimeZone()),
			CustomDateUtil.toISOString(event.getEndDate(), event.getTimeZone()),
			event.getIsAllDay(),
			event.getRecurrence(),
			event.getDescription(),
			event.getColor()
		);
	}
}
