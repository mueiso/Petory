package com.study.petory.domain.dailyQna.entity;

import lombok.Getter;

@Getter
public enum QuestionStatus {
	ACTIVE("정상"),
	INACTIVE("비활성화"),
	DELETED("관리자 삭제 대기");

	private final String displayName;

	QuestionStatus(String displayName) {
		this.displayName = displayName;
	}
}
