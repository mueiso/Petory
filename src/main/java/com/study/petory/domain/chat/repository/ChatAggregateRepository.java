package com.study.petory.domain.chat.repository;

import java.util.List;

import com.study.petory.domain.chat.entity.ChatRoom;

public interface ChatAggregateRepository {

	List<ChatRoom> findChatRoomsByUserId(Long userId, int page);
}
