package com.study.petory.domain.chat.service;

import com.study.petory.domain.chat.dto.request.ChatMessageSendRequestDto;
import com.study.petory.domain.chat.entity.ChatMessage;

public interface ChatMessageService {

	ChatMessage createChatMessage(ChatMessageSendRequestDto requestDto);
}
