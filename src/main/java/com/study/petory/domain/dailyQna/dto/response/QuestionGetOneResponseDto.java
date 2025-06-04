package com.study.petory.domain.dailyQna.dto.response;

import com.study.petory.domain.dailyQna.entity.Question;

import lombok.Getter;

@Getter
public class QuestionGetOneResponseDto {

	private final String question;

	private final String date;

	private QuestionGetOneResponseDto(String question, String date) {
		this.question = question;
		this.date = date;
	}

	public static QuestionGetOneResponseDto from(Question question) {
		return new QuestionGetOneResponseDto(
			question.getQuestion(),
			question.getDate());
	}
}
