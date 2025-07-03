package com.study.petory.domain.event.dto.response;

import java.util.List;

import com.study.petory.common.util.CustomDateUtil;
import com.study.petory.domain.event.entity.EventColor;
import com.study.petory.domain.event.entity.EventInstance;

import lombok.Getter;

@Getter
public class EventGetOneResponseDto {

	private final Long id;

	private final String title;

	private final String startDate;

	private final String endDate;

	private final boolean isAllDay;

	private final List<String> recurrence;

	private final String description;

	private final EventColor color;

	private EventGetOneResponseDto(Long id, String title, String startDate, String endDate, boolean isAllDay,
		List<String> recurrence, String description, EventColor color) {
		this.id = id;
		this.title = title;
		this.startDate = startDate;
		this.endDate = endDate;
		this.isAllDay = isAllDay;
		this.recurrence = recurrence;
		this.description = description;
		this.color = color;
	}

	public static EventGetOneResponseDto from(EventInstance event) {
		return new EventGetOneResponseDto(
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
