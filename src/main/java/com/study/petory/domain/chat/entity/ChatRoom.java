package com.study.petory.domain.chat.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import org.springframework.data.annotation.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Document(collection = "chatroom")
@NoArgsConstructor
public class ChatRoom {

	@Id
	private ObjectId id;

	private Long tradeBoardId;

	private Long sellerId;

	private Long customerId;

	private List<ChatMessage> messages = new ArrayList<>();

	private LocalDateTime lastMessageDate;

	private boolean sellerExist;

	private boolean customerExist;

	@Builder
	public ChatRoom(Long tradeBoardId, Long sellerId, Long customerId) {
		this.tradeBoardId = tradeBoardId;
		this.sellerId = sellerId;
		this.customerId = customerId;
		this.sellerExist = true;
		this.customerExist = true;
	}

	public void addMessage(ChatMessage message) {
		this.messages.add(message);
		this.lastMessageDate = message.getCreatedAt();
	}

	public boolean isMember(Long userId) {
		return this.sellerId.equals(userId) || this.customerId.equals(userId);
	}

	public boolean isSeller(Long userId) {
		return this.sellerId.equals(userId);
	}

	public void leaveChatRoom(Long userId) {
		if (this.isSeller(userId)) {
			this.sellerExist = false;
		} else {
			this.customerExist = false;
		}
	}
}
