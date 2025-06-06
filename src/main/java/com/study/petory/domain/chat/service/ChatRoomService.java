package com.study.petory.domain.chat.service;

import org.springframework.data.domain.Slice;

import com.study.petory.domain.chat.dto.response.ChatRoomCreateResponseDto;
import com.study.petory.domain.chat.dto.response.ChatRoomAllGetResponseDto;
import com.study.petory.domain.chat.dto.response.ChatRoomGetResponseDto;

public interface ChatRoomService {

	ChatRoomCreateResponseDto saveChatRoom(Long tradeBoardId);

	Slice<ChatRoomAllGetResponseDto> findAllChatRoom();

	ChatRoomGetResponseDto findByChatRoomId(String chatRoomId);

	void deleteChatRoom(String chatRoomId);
}
