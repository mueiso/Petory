package com.study.petory.domain.chat.entity;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Document(collection = "chatroom")
@NoArgsConstructor
public class ChatRoom {

	@Id
	private ObjectId id;

	private Long sellerId;

	private Long customerId;

	private Long tradeBoardId;

	private List<ChatMessage> messages = new ArrayList<>();

	@Builder
	public ChatRoom(Long sellerId, Long customerId, Long tradeBoardId) {
		this.sellerId = sellerId;
		this.customerId = customerId;
		this.tradeBoardId = tradeBoardId;
	}

	public void addMessage(ChatMessage message) {
		this.messages.add(message);
	}
}
