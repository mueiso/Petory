package com.study.petory.domain.chat.service;

import com.study.petory.domain.chat.dto.response.ChatRoomCreateResponseDto;

public interface ChatRoomService {

	ChatRoomCreateResponseDto saveChatRoom(Long tradeBoardId);
}
