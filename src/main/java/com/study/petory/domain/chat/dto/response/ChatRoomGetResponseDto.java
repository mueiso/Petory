package com.study.petory.domain.chat.dto.response;

import com.study.petory.domain.chat.entity.ChatRoom;

import lombok.Getter;

@Getter
public class ChatRoomGetResponseDto {

	private final String tradeBoardTitle;

	private final String lastMessageId;

	public ChatRoomGetResponseDto(ChatRoom chatRoom) {
		this.tradeBoardTitle = chatRoom.getTradeBoardTitle();
		this.lastMessageId = chatRoom.getLastMessageId();
	}
}
