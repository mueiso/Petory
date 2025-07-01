package com.study.petory.domain.dailyQna.dto.response;

import java.time.LocalDateTime;

import com.study.petory.domain.dailyQna.entity.DailyQuestion;

import lombok.Getter;

@Getter
public class DailyQuestionGetDeletedResponseDto {

	private final String question;

	private final String date;

	private final LocalDateTime deletedAt;

	private DailyQuestionGetDeletedResponseDto(String question, String date, LocalDateTime deletedAt) {
		this.question = question;
		this.date = date;
		this.deletedAt = deletedAt;
	}

	public static DailyQuestionGetDeletedResponseDto from(DailyQuestion dailyQuestion) {
		return new DailyQuestionGetDeletedResponseDto(
			dailyQuestion.getQuestion(),
			dailyQuestion.getDate(),
			dailyQuestion.getDeletedAt()
		);
	}
}
