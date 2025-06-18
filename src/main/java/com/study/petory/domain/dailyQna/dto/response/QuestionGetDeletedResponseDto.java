package com.study.petory.domain.dailyQna.dto.response;

import java.time.LocalDateTime;

import com.study.petory.domain.dailyQna.entity.Question;

import lombok.Getter;

@Getter
public class QuestionGetDeletedResponseDto {

	private final String content;

	private final String date;

	private final LocalDateTime deletedAt;

	private QuestionGetDeletedResponseDto(String content, String date, LocalDateTime deletedAt) {
		this.content = content;
		this.date = date;
		this.deletedAt = deletedAt;
	}

	public static QuestionGetDeletedResponseDto from(Question question) {
		return new QuestionGetDeletedResponseDto(
			question.getContent(),
			question.getDate(),
			question.getDeletedAt()
		);
	}
}
