package com.study.petory.domain.dailyQna.dto.response;

import java.time.LocalDateTime;

import com.study.petory.domain.dailyQna.entity.DailyAnswer;

import lombok.Getter;

@Getter
public class DailyAnswerGetDeletedResponse {

	private final String answer;

	private final LocalDateTime createdAt;

	private final LocalDateTime deletedAt;

	private DailyAnswerGetDeletedResponse(String answer, LocalDateTime createdAt, LocalDateTime deletedAt) {
		this.answer = answer;
		this.createdAt = createdAt;
		this.deletedAt = deletedAt;
	}

	public static DailyAnswerGetDeletedResponse from(DailyAnswer dailyAnswer) {
		return new DailyAnswerGetDeletedResponse(
			dailyAnswer.getAnswer(),
			dailyAnswer.getCreatedAt(),
			dailyAnswer.getUpdatedAt()
		);
	}
}
