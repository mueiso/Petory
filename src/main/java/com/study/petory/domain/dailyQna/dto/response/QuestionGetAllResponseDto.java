package com.study.petory.domain.dailyQna.dto.response;

import com.study.petory.domain.dailyQna.entity.Question;

import lombok.Getter;

@Getter
public class QuestionGetAllResponseDto {

	private final String question;

	private final String date;

	private QuestionGetAllResponseDto(String question, String date) {
		this.question = question;
		this.date = date;
	}

	public static QuestionGetAllResponseDto from(Question question) {
		return new QuestionGetAllResponseDto(
			question.getQuestion(),
			question.getDate());
	}
}
