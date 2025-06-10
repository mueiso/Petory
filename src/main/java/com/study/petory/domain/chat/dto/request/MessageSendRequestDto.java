package com.study.petory.domain.chat.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MessageSendRequestDto {

	private final String chatRoomId;

	private final Long senderId;

	private final String message;
}