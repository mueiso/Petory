package com.study.petory.domain.dailyQna.dto.response;

import java.time.LocalDateTime;

import com.study.petory.domain.dailyQna.entity.Question;

import lombok.Getter;

@Getter
public class QuestionGetInactiveResponse {

	private final String question;

	private final String date;

	private final LocalDateTime inactiveTime;

	private QuestionGetInactiveResponse(String question, String date, LocalDateTime inactiveTime) {
		this.question = question;
		this.date = date;
		this.inactiveTime = inactiveTime;
	}

	public static QuestionGetInactiveResponse from(Question question) {
		return new QuestionGetInactiveResponse(
			question.getQuestion(),
			question.getDate(),
			question.getUpdatedAt()
		);
	}
}
