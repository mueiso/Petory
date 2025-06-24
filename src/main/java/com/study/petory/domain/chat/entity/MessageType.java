package com.study.petory.domain.chat.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MessageType {

	TEXT("텍스트"),
	IMAGE("사진");

	private final String description;
}
