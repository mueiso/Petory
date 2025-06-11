package com.study.petory.domain.dailyQna.dto.response;

import java.time.LocalDateTime;

import com.study.petory.domain.dailyQna.entity.Question;

import lombok.Getter;

@Getter
public class QuestionGetInactiveResponseDto {

	private final String question;

	private final String date;

	private final LocalDateTime inactiveTime;

	private QuestionGetInactiveResponseDto(String question, String date, LocalDateTime inactiveTime) {
		this.question = question;
		this.date = date;
		this.inactiveTime = inactiveTime;
	}

	public static QuestionGetInactiveResponseDto from(Question question) {
		return new QuestionGetInactiveResponseDto(
			question.getQuestion(),
			question.getDate(),
			question.getUpdatedAt()
		);
	}
}
