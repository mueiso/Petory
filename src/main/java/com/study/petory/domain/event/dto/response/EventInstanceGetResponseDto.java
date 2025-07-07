package com.study.petory.domain.event.dto.response;

import com.study.petory.common.util.CustomDateUtil;
import com.study.petory.domain.event.entity.EventColor;
import com.study.petory.domain.event.entity.EventInstance;

import lombok.Getter;

@Getter
public class EventInstanceGetResponseDto {

	private final Long id;

	private final String title;

	private final String startDate;

	private final String endDate;

	private final boolean isAllDay;

	private final EventColor color;

	private EventInstanceGetResponseDto(Long id, String title, String startDate, String endDate,
		boolean isAllDay, EventColor color) {
		this.id = id;
		this.title = title;
		this.startDate = startDate;
		this.endDate = endDate;
		this.isAllDay = isAllDay;
		this.color = color;
	}

	public static EventInstanceGetResponseDto from(EventInstance event) {
		return new EventInstanceGetResponseDto(
			event.getId(),
			event.getTitle(),
			CustomDateUtil.toISOString(event.getStartDate(), event.getTimeZone()),
			CustomDateUtil.toISOString(event.getEndDate(), event.getTimeZone()),
			event.getIsAllDay(),
			event.getColor()
		);
	}
}
