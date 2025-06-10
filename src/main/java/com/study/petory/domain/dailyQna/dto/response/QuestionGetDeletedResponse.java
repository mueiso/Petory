package com.study.petory.domain.dailyQna.dto.response;

import java.time.LocalDateTime;

import com.study.petory.domain.dailyQna.entity.Question;

import lombok.Getter;

@Getter
public class QuestionGetDeletedResponse {

	private final String question;

	private final String date;

	private final LocalDateTime deletedAt;

	private QuestionGetDeletedResponse(String question, String date, LocalDateTime deletedAt) {
		this.question = question;
		this.date = date;
		this.deletedAt = deletedAt;
	}

	public static QuestionGetDeletedResponse from(Question question) {
		return new QuestionGetDeletedResponse(
			question.getQuestion(),
			question.getDate(),
			question.getDeletedAt()
		);
	}
}
