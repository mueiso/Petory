package com.study.petory.domain.dailyqna.dto.response;

import java.time.LocalDateTime;

import com.study.petory.domain.dailyqna.entity.DailyQuestion;

import lombok.Getter;

@Getter
public class DailyQuestionGetInactiveResponseDto {

	private final String question;

	private final String date;

	private final LocalDateTime inactiveTime;

	private DailyQuestionGetInactiveResponseDto(String question, String date, LocalDateTime inactiveTime) {
		this.question = question;
		this.date = date;
		this.inactiveTime = inactiveTime;
	}

	public static DailyQuestionGetInactiveResponseDto from(DailyQuestion dailyQuestion) {
		return new DailyQuestionGetInactiveResponseDto(
			dailyQuestion.getQuestion(),
			dailyQuestion.getDate(),
			dailyQuestion.getUpdatedAt()
		);
	}
}
