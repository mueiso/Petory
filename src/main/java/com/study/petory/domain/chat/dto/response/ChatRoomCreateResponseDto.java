package com.study.petory.domain.chat.dto.response;

import com.study.petory.domain.chat.entity.ChatRoom;

import lombok.Getter;

@Getter
public class ChatRoomCreateResponseDto {

	private final String chatRoomId;

	private final Long tradeBoardId;

	private final Long sellerId;

	private final Long customerId;

	public ChatRoomCreateResponseDto(ChatRoom chatRoom) {
		this.chatRoomId = chatRoom.getId().toHexString();
		this.tradeBoardId = chatRoom.getTradeBoardId();
		this.sellerId = chatRoom.getSellerId();
		this.customerId = chatRoom.getCustomerId();
	}
}
