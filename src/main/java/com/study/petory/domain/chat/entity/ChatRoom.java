package com.study.petory.domain.chat.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
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
	private String id;

	private Long sellerId;

	private Long customerId;

	private Long tradeBoardId;

	private boolean isDeleted = false;

	@CreatedDate
	private LocalDateTime createdAt;

	@Builder
	public ChatRoom(Long sellerId, Long customerId, Long tradeBoardId) {
		this.sellerId = sellerId;
		this.customerId = customerId;
		this.tradeBoardId = tradeBoardId;
	}

	public void deactivateChatRoom() {
		this.isDeleted = true;
	}

	public boolean isEqualSeller(Long userId) {
		return this.sellerId.equals(userId);
	}
}
