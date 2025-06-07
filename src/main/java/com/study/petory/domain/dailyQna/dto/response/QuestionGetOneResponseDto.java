package com.study.petory.domain.dailyQna.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.study.petory.domain.dailyQna.entity.Question;

import lombok.Getter;

@Getter
public class QuestionGetOneResponseDto {

	private final String question;

	private final String date;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private final LocalDateTime deletedAt;

	private QuestionGetOneResponseDto(String question, String date, LocalDateTime deletedAt) {
		this.question = question;
		this.date = date;
		if (deletedAt != null) {
			this.deletedAt = deletedAt;
		} else {
			this.deletedAt = null;
		}
	}

	public static QuestionGetOneResponseDto from(Question question) {
		return new QuestionGetOneResponseDto(
			question.getQuestion(),
			question.getDate(),
			question.getDeletedAt()
		);
	}
}
