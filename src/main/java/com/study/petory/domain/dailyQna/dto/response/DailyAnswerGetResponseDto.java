package com.study.petory.domain.dailyQna.dto.response;

import java.time.LocalDateTime;

import com.study.petory.domain.dailyQna.entity.DailyAnswer;

import lombok.Getter;

@Getter
public class DailyAnswerGetResponseDto {

	private final String answer;

	private final LocalDateTime createdAt;

	private DailyAnswerGetResponseDto(String answer, LocalDateTime createdAt) {
		this.answer = answer;
		this.createdAt = createdAt;
	}

	public static DailyAnswerGetResponseDto from(DailyAnswer dailyAnswer) {
		return new DailyAnswerGetResponseDto(
			dailyAnswer.getAnswer(),
			dailyAnswer.getCreatedAt()
		);
	}

	public static DailyAnswerGetResponseDto from(String answer, LocalDateTime createdAt) {
		return new DailyAnswerGetResponseDto(
			answer,
			createdAt
		);
	}
}
