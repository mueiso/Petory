package com.study.petory.domain.chat.dto.response;

import com.study.petory.domain.chat.entity.ChatRoom;

import lombok.Getter;

@Getter
public class ChatRoomGetAllResponseDto {

	private final String chatRoomId;

	private final Long opponentId;

	public ChatRoomGetAllResponseDto(ChatRoom chatRoom, Long userId) {
		this.chatRoomId = chatRoom.getId().toHexString();
		this.opponentId = chatRoom.getSellerId().equals(userId) ? chatRoom.getCustomerId() : chatRoom.getSellerId();
	}

}
