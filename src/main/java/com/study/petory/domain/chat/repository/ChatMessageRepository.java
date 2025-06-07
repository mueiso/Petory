package com.study.petory.domain.chat.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.study.petory.domain.chat.entity.ChatMessage;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {

	List<ChatMessage> findAllByChatRoomId(String chatRoomId);

}
