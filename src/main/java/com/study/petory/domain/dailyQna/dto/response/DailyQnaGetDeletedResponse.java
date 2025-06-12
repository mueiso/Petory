package com.study.petory.domain.dailyQna.dto.response;

import java.time.LocalDateTime;

import com.study.petory.domain.dailyQna.entity.DailyQna;

import lombok.Getter;

@Getter
public class DailyQnaGetDeletedResponse {

	private final String answer;

	private final LocalDateTime createdAt;

	private final LocalDateTime deletedAt;

	private DailyQnaGetDeletedResponse(String answer, LocalDateTime createdAt, LocalDateTime deletedAt) {
		this.answer = answer;
		this.createdAt = createdAt;
		this.deletedAt = deletedAt;
	}

	public static DailyQnaGetDeletedResponse from(DailyQna dailyQna) {
		return new DailyQnaGetDeletedResponse(
			dailyQna.getAnswer(),
			dailyQna.getCreatedAt(),
			dailyQna.getUpdatedAt()
		);
	}
}
