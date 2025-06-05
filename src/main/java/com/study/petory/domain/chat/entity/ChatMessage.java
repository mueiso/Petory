package com.study.petory.domain.chat.entity;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Document(collection = "chatMessage")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

	@Id
	private String id;

	private String chatRoomId;

	private Long senderId;

	private String message;

	private LocalDateTime createdAt;
}
