package com.study.petory.domain.dailyQna.dto.response;

import java.time.LocalDateTime;

import com.study.petory.domain.dailyQna.entity.Question;
import com.study.petory.domain.dailyQna.entity.QuestionStatus;

import lombok.Getter;

@Getter
public class QuestionGetAllResponseDto {

	private final String question;

	private final String date;

	private QuestionStatus questionStatus;

	private LocalDateTime updatedAt;

	private LocalDateTime deletedAt;


	private QuestionGetAllResponseDto(String question, String date, QuestionStatus questionStatus, LocalDateTime updatedAt, LocalDateTime deletedAt) {
		this.question = question;
		this.date = date;
		if (questionStatus == QuestionStatus.INACTIVE) {
			this.questionStatus = questionStatus;
			this.updatedAt = updatedAt;
		}
		if (questionStatus == QuestionStatus.DELETED) {
			this.questionStatus = questionStatus;
			this.deletedAt = deletedAt;
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
