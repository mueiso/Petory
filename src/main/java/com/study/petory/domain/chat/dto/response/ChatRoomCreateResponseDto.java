package com.study.petory.domain.chat.dto.response;

import java.time.LocalDateTime;

import com.study.petory.domain.chat.entity.ChatRoom;
import com.study.petory.domain.tradeBoard.entity.TradeBoard;

import lombok.Getter;

@Getter
public class ChatRoomCreateResponseDto {

	private final String chatRoomId;

	private final Long tradeBoardId;

	private final Long sellerId;

	private final Long customerId;

	private final LocalDateTime createdAt;

	public ChatRoomCreateResponseDto(ChatRoom chatRoom) {
		this.chatRoomId = chatRoom.getId();
		this.tradeBoardId = chatRoom.getTradeBoardId();
		this.sellerId = chatRoom.getSellerId();
		this.customerId = chatRoom.getCustomerId();
		this.createdAt = chatRoom.getCreatedAt();
	}
}