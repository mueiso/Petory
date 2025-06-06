package com.study.petory.domain.chat.service;

import org.springframework.data.domain.Page;

import com.study.petory.domain.chat.dto.response.ChatRoomCreateResponseDto;
import com.study.petory.domain.chat.dto.response.ChatRoomAllGetResponseDto;
import com.study.petory.domain.chat.dto.response.ChatRoomGetResponseDto;

public interface ChatRoomService {

	ChatRoomCreateResponseDto saveChatRoom(Long tradeBoardId);

	Page<ChatRoomAllGetResponseDto> findAllChatRoom(int page);

	ChatRoomGetResponseDto findByChatRoomId(String chatRoomId);

	void deleteChatRoom(String chatRoomId);
}
