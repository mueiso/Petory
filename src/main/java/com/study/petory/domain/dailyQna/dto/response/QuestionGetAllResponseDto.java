package com.study.petory.domain.dailyQna.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.study.petory.domain.dailyQna.entity.Question;
import com.study.petory.domain.dailyQna.entity.QuestionStatus;

import lombok.Getter;

@Getter
public class QuestionGetAllResponseDto {

	private final String question;

	private final String date;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private QuestionStatus questionStatus;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private LocalDateTime updatedAt;

	private QuestionGetAllResponseDto(String question, String date, QuestionStatus questionStatus, LocalDateTime updatedAt) {
		this.question = question;
		this.date = date;
		if (questionStatus == QuestionStatus.INACTIVE) {
			this.questionStatus = questionStatus;
			this.updatedAt = updatedAt;
		}
	}

	public static QuestionGetAllResponseDto from(Question question) {
		return new QuestionGetAllResponseDto(
			question.getQuestion(),
			question.getDate(),
			question.getQuestionStatus(),
			question.getUpdatedAt()
		);
	}
}
