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

	private Long sellerId; // 판매자 닉네임

	private Long customerId; // 구매자 닉네임

	private String tradeBoardTitle;

	private String tradeBoardUrl; // 구매하려는 상품 url

	private String lastMessageId;

	private boolean isDeleted = false;

	@CreatedDate
	private LocalDateTime createdAt;

	@Builder
	public ChatRoom(Long sellerId, Long customerId, String tradeBoardTitle, String tradeBoardUrl, String lastMessageId) {
		this.sellerId = sellerId;
		this.customerId = customerId;
		this.tradeBoardTitle = tradeBoardTitle;
		this.tradeBoardUrl = tradeBoardUrl;
		this.lastMessageId = lastMessageId;
	}

	public void deactivateChatRoom() {
		this.isDeleted = true;
	}
}
