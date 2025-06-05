package com.study.petory.domain.chat.dto.response;

import java.time.LocalDateTime;

import com.study.petory.domain.chat.entity.ChatRoom;

import lombok.Getter;

@Getter
public class ChatRoomCreateResponseDto {

	private final String chatRoomId;

	private final Long sellerId;

	private final Long customerId;

	private final String tradeBoardTitle;

	private final String tradeBoardUrl;

	private final LocalDateTime createdAt;

	public ChatRoomCreateResponseDto(ChatRoom chatRoom) {
		this.chatRoomId = chatRoom.getId();
		this.sellerId = chatRoom.getSellerId();
		this.customerId = chatRoom.getCustomerId();
		this.tradeBoardTitle = chatRoom.getTradeBoardTitle();
		this.tradeBoardUrl = chatRoom.getTradeBoardUrl();
		this.createdAt = chatRoom.getCreatedAt();
	}
}