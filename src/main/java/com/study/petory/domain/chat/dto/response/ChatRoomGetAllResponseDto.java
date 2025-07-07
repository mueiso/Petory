package com.study.petory.domain.chat.dto.response;

import com.study.petory.domain.chat.entity.ChatRoom;

import lombok.Getter;

@Getter
public class ChatRoomGetAllResponseDto {

	private final String chatRoomId;

	private final Long opponentId;

	public ChatRoomGetAllResponseDto(ChatRoom chatRoom, Long userId) {
		this.chatRoomId = chatRoom.getId().toHexString();
		// 판매자가 자신이라면 구매자를 상대방 아이디에 등록, 아니라면 판매자 아이디를 상대방 아이디에 등록
		this.opponentId = chatRoom.getSellerId().equals(userId) ? chatRoom.getCustomerId() : chatRoom.getSellerId();
	}

}
