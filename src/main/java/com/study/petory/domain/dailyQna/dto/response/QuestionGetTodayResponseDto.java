package com.study.petory.domain.dailyQna.dto.response;

import com.study.petory.domain.dailyQna.entity.Question;
import com.study.petory.domain.dailyQna.entity.QuestionStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(force = true)
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
			question.getDate()
		);
	}
}
