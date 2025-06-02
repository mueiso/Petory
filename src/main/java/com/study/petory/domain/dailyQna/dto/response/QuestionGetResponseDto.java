package com.study.petory.domain.dailyQna.dto.response;

import com.study.petory.domain.dailyQna.entity.Question;

import lombok.Getter;

@Getter
public class QuestionGetResponseDto {

	private final String question;

	private final String date;

	private QuestionGetResponseDto(String question, String date) {
		this.question = question;
		this.date = date;
	}

	public static QuestionGetResponseDto from(Question question) {
		return new QuestionGetResponseDto(
			question.getQuestion(),
			question.getDate());
	}
}
