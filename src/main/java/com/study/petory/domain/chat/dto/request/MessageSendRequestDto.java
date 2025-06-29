package com.study.petory.domain.chat.dto.request;

import com.study.petory.domain.chat.entity.MessageType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MessageSendRequestDto {

	private final String chatRoomId;

	private final MessageType messageType;

	private final String content;
}