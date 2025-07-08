package com.study.petory.domain.event.dto.request;

import java.util.List;

import com.study.petory.common.util.ValidEnum;
import com.study.petory.domain.event.entity.EventColor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EventCreateRequestDto {

	@Size(max = 100, message = "최대 100자 입력할 수 있습니다.")
	@NotBlank(message = "제목은 필수 항목입니다.")
	private String title;

	@Size(max = 30, message = "최대 30자 입력할 수 있습니다.")
	@NotBlank(message = "시작 일은 필수 항목입니다.")
	private String startDate;

	@Size(max = 30, message = "최대 30자 입력할 수 있습니다.")
	@NotBlank(message = "종료 일은 필수 항목입니다.")
	private String endDate;

	@Size(max = 20, message = "최대 20자 입력할 수 있습니다.")
	@NotBlank(message = "타임 존은 필수 항목입니다.")
	private String timeZone;

	private Boolean isAllDay;

	private List<String> recurrence;

	@Size(max = 300, message = "최대 300자 입력할 수 있습니다.")
	private String description;

	@ValidEnum(enumClass = EventColor.class, ignoreCase = true, message = "지원하지 않는 색상입니다.")
	private EventColor color;
}
