package com.study.petory.domain.chat.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import com.study.petory.domain.chat.dto.request.ChatMessageSendRequestDto;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Document(collection = "chatMessage")
@NoArgsConstructor
public class ChatMessage {

	@Id
	private String id;

	private String chatRoomId;

	private Long senderId;

	private String message;

	@CreatedDate
	private LocalDateTime createdAt;

	@Builder
	public ChatMessage(String chatRoomId, Long senderId, String message) {
		this.chatRoomId = chatRoomId;
		this.senderId = senderId;
		this.message = message;
	}
}
