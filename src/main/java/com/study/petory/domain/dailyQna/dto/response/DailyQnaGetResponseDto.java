package com.study.petory.domain.dailyQna.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DailyQnaGetResponseDto {

	private final String answer;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private final LocalDateTime createdAt;
}
