package com.study.petory.domain.tradeboard.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TradeBoardStatus {

	FOR_SALE("판매중"),
	SOLD_OUT("판매 완료"),
	HIDDEN("게시글 숨기기"),
	DELETED("삭제");

	private final String description;
}
