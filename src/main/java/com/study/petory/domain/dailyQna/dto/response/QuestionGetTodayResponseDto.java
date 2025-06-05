package com.study.petory.domain.dailyQna.dto.response;

import com.study.petory.domain.dailyQna.entity.Question;

import lombok.Getter;

@Getter
public class QuestionGetTodayResponseDto {

	private final String question;

	private final String date;

	private QuestionGetTodayResponseDto(String question, String date) {
		this.question = question;
		this.date = date;
	}

	public static QuestionGetTodayResponseDto from(Question question) {
		return new QuestionGetTodayResponseDto(
			question.getQuestion(),
			question.getDate());
	}
}
