package com.study.petory.domain.chat.service;

import com.study.petory.domain.chat.dto.request.MessageSendRequestDto;
import com.study.petory.domain.chat.entity.ChatMessage;

public interface ChatService {

	ChatMessage createMessage(MessageSendRequestDto requestDto);
}
