package com.study.petory.domain.chat.dto.response;

import java.util.List;

import com.study.petory.domain.chat.entity.ChatMessage;
import com.study.petory.domain.chat.entity.ChatRoom;

import lombok.Getter;

@Getter
public class ChatRoomGetResponseDto {

	private final String chatRoomId;

	private final String tradeBoardTitle;

	private final String tradeBoardUrl;

	private final List<ChatMessage> messages;

	public ChatRoomGetResponseDto(ChatRoom chatRoom, List<ChatMessage> messages) {
		this.chatRoomId = chatRoom.getId();
		this.tradeBoardTitle = chatRoom.getTradeBoardTitle();
		this.tradeBoardUrl = chatRoom.getTradeBoardUrl();
		this.messages = messages;
	}
}
