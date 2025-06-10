package com.study.petory.domain.dailyQna.dto.response;

import java.time.LocalDateTime;

import com.study.petory.domain.dailyQna.entity.DailyQna;

import lombok.Getter;

@Getter
public class DailyQnaGetResponseDto {

	private final String answer;

	private final LocalDateTime createdAt;

	private DailyQnaGetResponseDto(String answer, LocalDateTime createdAt) {
		this.answer = answer;
		this.createdAt = createdAt;
	}

	public static DailyQnaGetResponseDto from(DailyQna dailyQna) {
		return new DailyQnaGetResponseDto(
			dailyQna.getAnswer(),
			dailyQna.getCreatedAt()
		);
	}
}
