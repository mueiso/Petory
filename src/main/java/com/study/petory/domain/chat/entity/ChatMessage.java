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

	private MessageType messageType;

	private String content;

	private LocalDateTime createdAt;

	@Builder
	public ChatMessage(Long senderId, MessageType messageType, String content, String senderNickname) {
		this.senderId = senderId;
		this.messageType = messageType;
		this.content = content;
		this.senderNickname = senderNickname;
		this.createdAt = LocalDateTime.now();
	}
}
