package com.study.petory.domain.chat.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.study.petory.domain.chat.dto.request.MessageSendRequestDto;
import com.study.petory.domain.chat.dto.response.ChatRoomCreateResponseDto;
import com.study.petory.domain.chat.dto.response.ChatRoomGetAllResponseDto;
import com.study.petory.domain.chat.dto.response.ChatRoomGetResponseDto;
import com.study.petory.domain.chat.entity.ChatMessage;

public interface ChatService {

	ChatMessage createMessage(Long userId, MessageSendRequestDto requestDto);

	ChatRoomCreateResponseDto saveChatRoom(Long userId, Long tradeBoardId);

	List<ChatRoomGetAllResponseDto> findAllChatRoom(Long userId, Pageable pageable);

	ChatRoomGetResponseDto findChatRoomById(Long userId, String chatRoomId);

	void leaveChatRoomById(Long userId, String chatRoomId);
}
