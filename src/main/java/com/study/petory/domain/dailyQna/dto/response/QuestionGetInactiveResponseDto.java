package com.study.petory.domain.dailyQna.dto.response;

import java.time.LocalDateTime;

import com.study.petory.domain.dailyQna.entity.Question;

import lombok.Getter;

@Getter
public class QuestionGetInactiveResponseDto {

	private final String content;

	private final String date;

	private final LocalDateTime inactiveTime;

	private QuestionGetInactiveResponseDto(String content, String date, LocalDateTime inactiveTime) {
		this.content = content;
		this.date = date;
		this.inactiveTime = inactiveTime;
	}

	public static QuestionGetInactiveResponseDto from(Question question) {
		return new QuestionGetInactiveResponseDto(
			question.getContent(),
			question.getDate(),
			question.getUpdatedAt()
		);
	}
}
