package com.study.petory.domain.dailyQna.dto.response;

import com.study.petory.domain.dailyQna.entity.Question;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(force = true)
public class QuestionGetTodayResponseDto {

	private final String content;

	private final String date;

	private QuestionGetTodayResponseDto(String content, String date) {
		this.content = content;
		this.date = date;
	}

	public static QuestionGetTodayResponseDto from(Question question) {
		return new QuestionGetTodayResponseDto(
			question.getContent(),
			question.getDate()
		);
	}
}
