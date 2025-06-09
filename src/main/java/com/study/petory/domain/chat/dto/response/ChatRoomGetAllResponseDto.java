package com.study.petory.domain.chat.dto.response;

import com.study.petory.domain.chat.entity.ChatRoom;

import lombok.Getter;

@Getter
public class ChatRoomGetAllResponseDto {

	private final String chatRoomId;

	private final Long tradeBoardId;

	private final String opponentUserNickname;

	public ChatRoomGetAllResponseDto(ChatRoom chatRoom, String opponentUserNickname) {
		this.chatRoomId = chatRoom.getId();
		this.tradeBoardId = chatRoom.getTradeBoardId();
		this.opponentUserNickname = opponentUserNickname;
	}
}
