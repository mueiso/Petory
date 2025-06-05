package com.study.petory.domain.chat.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.study.petory.domain.chat.entity.ChatRoom;

public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {
}
