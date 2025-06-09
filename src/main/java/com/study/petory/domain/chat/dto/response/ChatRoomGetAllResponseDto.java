package com.study.petory.domain.chat.dto.response;

import com.study.petory.domain.chat.entity.ChatRoom;

import lombok.Getter;

@Getter
public class ChatRoomGetAllResponseDto {

	private final String chatRoomId;

	private final Long tradeBoardId;

	private final Long opponentUserId;

	public ChatRoomGetAllResponseDto(ChatRoom chatRoom, Long opponentUserId) {
		this.chatRoomId = chatRoom.getId();
		this.tradeBoardId = chatRoom.getTradeBoardId();
		this.opponentUserId = opponentUserId;
	}
}
