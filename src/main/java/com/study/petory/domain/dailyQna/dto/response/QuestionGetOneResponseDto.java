package com.study.petory.domain.dailyQna.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.study.petory.domain.dailyQna.entity.Question;
import com.study.petory.domain.dailyQna.entity.QuestionStatus;

import lombok.Getter;

@Getter
public class QuestionGetOneResponseDto {

	private final String content;

	private final String date;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private QuestionStatus questionStatus;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private LocalDateTime updatedAt;

	private QuestionGetOneResponseDto(String content, String date, QuestionStatus questionStatus,
		LocalDateTime updatedAt) {
		this.content = content;
		this.date = date;
		if (questionStatus == QuestionStatus.INACTIVE) {
			this.questionStatus = questionStatus;
			this.updatedAt = updatedAt;
		}
	}

	public static QuestionGetOneResponseDto from(Question question) {
		return new QuestionGetOneResponseDto(
			question.getContent(),
			question.getDate(),
			question.getQuestionStatus(),
			question.getUpdatedAt()
		);
	}
}
