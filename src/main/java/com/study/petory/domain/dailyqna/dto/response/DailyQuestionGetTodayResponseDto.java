package com.study.petory.domain.dailyqna.dto.response;

import com.study.petory.domain.dailyqna.entity.DailyQuestion;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(force = true)
public class DailyQuestionGetTodayResponseDto {

	private final String question;

	private final String date;

	private DailyQuestionGetTodayResponseDto(String question, String date) {
		this.question = question;
		this.date = date;
	}

	public static DailyQuestionGetTodayResponseDto from(DailyQuestion dailyQuestion) {
		return new DailyQuestionGetTodayResponseDto(
			dailyQuestion.getQuestion(),
			dailyQuestion.getDate()
		);
	}
}
