package com.study.petory.domain.event.entity;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class RecurrenceInfo {
	private String rrule;
	private LocalDateTime recurrenceEnd;
	private String rDate;
	private String exDate;
}
