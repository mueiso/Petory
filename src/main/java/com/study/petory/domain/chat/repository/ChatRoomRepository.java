package com.study.petory.domain.chat.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.study.petory.domain.chat.entity.ChatRoom;

public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {

	ChatRoom findByTradeBoardIdAndSellerId(Long tradeBoardId, Long sellerId);
}
