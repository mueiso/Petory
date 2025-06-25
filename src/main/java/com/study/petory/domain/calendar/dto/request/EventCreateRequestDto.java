package com.study.petory.domain.calendar.dto.request;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EventCreateRequestDto {

	@NotBlank(message = "title은 필수 항목입니다.")
	private String title;

	@NotBlank(message = "startDate일은 필수 항목입니다.")
	private LocalDateTime startDate;

	private LocalDateTime endDate;

	@NotBlank(message = "timeZone은 필수 항목입니다.")
	private String timeZone;

	private Boolean isAllDay;

	private List<String> recurrence;

	private String description;

	private String color;
}
