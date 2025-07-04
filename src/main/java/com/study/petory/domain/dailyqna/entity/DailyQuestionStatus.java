package com.study.petory.domain.dailyqna.entity;

import lombok.Getter;

@Getter
public enum DailyQuestionStatus {
	ACTIVE("정상"),
	INACTIVE("비활성화"),
	DELETED("관리자 삭제 대기");

	private final String displayName;

	DailyQuestionStatus(String displayName) {
		this.displayName = displayName;
	}
}
