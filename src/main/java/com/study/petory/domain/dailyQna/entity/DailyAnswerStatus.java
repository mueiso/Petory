package com.study.petory.domain.dailyQna.entity;

import lombok.Getter;

@Getter
public enum DailyAnswerStatus {
	ACTIVE("정상 답변"),
	HIDDEN("사용자 숨김 처리"),
	DELETED("관리자 삭제 대기");

	private final String displayName;

	DailyAnswerStatus(String displayName) {
		this.displayName = displayName;
	}
}