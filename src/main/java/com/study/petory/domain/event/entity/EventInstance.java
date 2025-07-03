package com.study.petory.domain.event.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.study.petory.common.util.CustomDateUtil;

import lombok.Builder;
import lombok.Getter;

@Getter
public class EventInstance {

	private Long id;

	private String title;

	private LocalDateTime startDate;

	private LocalDateTime endDate;

	private String timeZone;

	private Boolean isAllDay;

	private List<String> recurrence;

	private String rrule;

	private LocalDateTime recurrenceEnd;

	private String rDate;

	private String exDate;

	private String description;

	private EventColor color;

	@Builder
	private EventInstance(Long id, String title, LocalDateTime startDate, LocalDateTime endDate, String timeZone,
		boolean isAllDay, List<String> recurrence, String rrule, LocalDateTime recurrenceEnd,
		String rDate, String exDate, String description, EventColor color) {
		this.id = id;
		this.title = title;
		this.startDate = startDate;
		this.endDate = endDate;
		this.timeZone = timeZone;
		this.isAllDay = isAllDay;
		this.recurrence = recurrence;
		this.rrule = rrule;
		this.recurrenceEnd = recurrenceEnd;
		this.rDate = rDate;
		this.exDate = exDate;
		this.description = description;
		this.color = color;
	}

	public static EventInstance createInstanceEvent(Event event, LocalDateTime instanceTime) {
		LocalDateTime eventStart;
		LocalDateTime eventEnd;

		if (instanceTime != null) {
			eventStart = instanceTime;
			long dateDifference = CustomDateUtil.getDaysDifference(event.getStartDate(), event.getEndDate());
			eventEnd = instanceTime.plusDays(dateDifference);
		} else {
			eventStart = event.getStartDate();
			eventEnd = event.getEndDate();
		}

		if (event.getIsAllDay()) {
			eventEnd = eventEnd.plusDays(1);
		}

		return EventInstance.builder()
			.id(event.getId())
			.title(event.getTitle())
			.startDate(eventStart)
			.endDate(eventEnd)
			.timeZone(event.getTimeZone())
			.isAllDay(event.getIsAllDay())
			.recurrence(event.getRecurrence())
			.rrule(event.getRrule())
			.recurrenceEnd(event.getRecurrenceEnd())
			.rDate(event.getRDate())
			.exDate(event.getExDate())
			.description(event.getDescription())
			.color(event.getColor())
			.build();
	}
}
