package com.study.petory.domain.chat.dto.response;

import com.study.petory.domain.chat.entity.ChatRoom;

import lombok.Getter;

@Getter
public class ChatRoomAllGetResponseDto {

	private final String chatRoomId;

	private final String tradeBoardTitle;

	private final String lastMessageId;

	public ChatRoomAllGetResponseDto(ChatRoom chatRoom) {
		this.chatRoomId = chatRoom.getId();
		this.tradeBoardTitle = chatRoom.getTradeBoardTitle();
		this.lastMessageId = chatRoom.getLastMessage();
	}
}
