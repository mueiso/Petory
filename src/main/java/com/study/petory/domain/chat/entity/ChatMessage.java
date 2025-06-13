package com.study.petory.domain.chat.entity;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatMessage {

	private Long senderId;

	private String senderNickname;

	private String message;

	private LocalDateTime createdAt;

	@Builder
	public ChatMessage(Long senderId, String message, String senderNickname) {
		this.senderId = senderId;
		this.message = message;
		this.senderNickname = senderNickname;
		this.createdAt = LocalDateTime.now();
	}
}
