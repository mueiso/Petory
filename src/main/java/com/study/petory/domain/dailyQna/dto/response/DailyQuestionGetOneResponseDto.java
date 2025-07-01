package com.study.petory.domain.dailyQna.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.study.petory.domain.dailyQna.entity.DailyQuestion;
import com.study.petory.domain.dailyQna.entity.DailyQuestionStatus;

import lombok.Getter;

@Getter
public class DailyQuestionGetOneResponseDto {

	private final String question;

	private final String date;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private DailyQuestionStatus dailyQuestionStatus;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private LocalDateTime updatedAt;

	private DailyQuestionGetOneResponseDto(String question, String date, DailyQuestionStatus dailyQuestionStatus,
		LocalDateTime updatedAt) {
		this.question = question;
		this.date = date;
		if (dailyQuestionStatus == DailyQuestionStatus.INACTIVE) {
			this.dailyQuestionStatus = dailyQuestionStatus;
			this.updatedAt = updatedAt;
		}
	}

	public static DailyQuestionGetOneResponseDto from(DailyQuestion dailyQuestion) {
		return new DailyQuestionGetOneResponseDto(
			dailyQuestion.getQuestion(),
			dailyQuestion.getDate(),
			dailyQuestion.getDailyQuestionStatus(),
			dailyQuestion.getUpdatedAt()
		);
	}
}
