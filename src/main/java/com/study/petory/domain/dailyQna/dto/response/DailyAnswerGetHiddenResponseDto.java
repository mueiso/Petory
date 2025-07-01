package com.study.petory.domain.dailyQna.dto.response;

import java.time.LocalDateTime;

import com.study.petory.domain.dailyQna.entity.DailyAnswer;

import lombok.Getter;

@Getter
public class DailyAnswerGetHiddenResponseDto {

	private final String answer;

	private final LocalDateTime createdAt;

	private final LocalDateTime hiddenTime;

	private DailyAnswerGetHiddenResponseDto(String answer, LocalDateTime createdAt, LocalDateTime hiddenTime) {
		this.answer = answer;
		this.createdAt = createdAt;
		this.hiddenTime = hiddenTime;
	}

	public static DailyAnswerGetHiddenResponseDto from(DailyAnswer dailyAnswer) {
		return new DailyAnswerGetHiddenResponseDto(
			dailyAnswer.getAnswer(),
			dailyAnswer.getCreatedAt(),
			dailyAnswer.getUpdatedAt()
		);
	}
}
