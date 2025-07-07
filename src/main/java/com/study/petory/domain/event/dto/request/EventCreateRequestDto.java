package com.study.petory.domain.event.dto.request;

import java.util.List;

import com.study.petory.common.util.ValidEnum;
import com.study.petory.domain.event.entity.EventColor;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EventCreateRequestDto {

	@NotBlank(message = "제목은 필수 항목입니다.")
	private String title;

	@NotBlank(message = "시작 일은 필수 항목입니다.")
	private String startDate;

	@NotBlank(message = "종료 일은 필수 항목입니다.")
	private String endDate;

	@NotBlank(message = "타임 존은 필수 항목입니다.")
	private String timeZone;

	private Boolean isAllDay;

	private List<String> recurrence;

	private String description;

	@ValidEnum(enumClass = EventColor.class, ignoreCase = true, message = "지원하지 않는 색상입니다.")
	private EventColor color;
}
