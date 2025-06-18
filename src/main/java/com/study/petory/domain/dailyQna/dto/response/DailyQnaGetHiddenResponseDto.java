package com.study.petory.domain.dailyQna.dto.response;

import java.time.LocalDateTime;

import com.study.petory.domain.dailyQna.entity.DailyQna;

import lombok.Getter;

@Getter
public class DailyQnaGetHiddenResponseDto {

	private final String answer;

	private final LocalDateTime createdAt;

	private final LocalDateTime hiddenTime;

	private DailyQnaGetHiddenResponseDto(String answer, LocalDateTime createdAt, LocalDateTime hiddenTime) {
		this.answer = answer;
		this.createdAt = createdAt;
		this.hiddenTime = hiddenTime;
	}

	public static DailyQnaGetHiddenResponseDto from(DailyQna dailyQna) {
		return new DailyQnaGetHiddenResponseDto(
			dailyQna.getAnswer(),
			dailyQna.getCreatedAt(),
			dailyQna.getUpdatedAt()
		);
	}
}
