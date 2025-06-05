package com.study.petory.domain.dailyQna.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.study.petory.domain.dailyQna.entity.Question;

import lombok.Getter;

@Getter
public class QuestionGetAllResponseDto {

	private final String question;

	private final String date;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private final LocalDateTime deletedAt;

	private QuestionGetAllResponseDto(String question, String date, LocalDateTime deletedAt) {
		this.question = question;
		this.date = date;
		if (deletedAt != null) {
			this.deletedAt = deletedAt;
		} else {
			this.deletedAt = null;
		}
	}

	public static QuestionGetAllResponseDto from(Question question) {
		return new QuestionGetAllResponseDto(
			question.getQuestion(),
			question.getDate(),
			question.getDeletedAt()
		);
	}
}
