package com.study.petory.domain.chat.service;

import java.util.List;

import com.study.petory.domain.chat.dto.request.MessageSendRequestDto;
import com.study.petory.domain.chat.dto.response.ChatRoomCreateResponseDto;
import com.study.petory.domain.chat.dto.response.ChatRoomGetAllResponseDto;
import com.study.petory.domain.chat.dto.response.ChatRoomGetResponseDto;
import com.study.petory.domain.chat.entity.ChatMessage;

public interface ChatService {

	ChatMessage createMessage(MessageSendRequestDto requestDto);

	ChatRoomCreateResponseDto saveChatRoom(Long tradeBoardId);

	List<ChatRoomGetAllResponseDto> findAllChatRoom(int page);

	ChatRoomGetResponseDto findChatRoomById(String chatRoomId);
}
