package com.study.petory.domain.chat.repository;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.study.petory.domain.chat.entity.ChatRoom;

public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {

	ChatRoom findByTradeBoardIdAndSellerId(Long tradeBoardId, Long sellerId);

	Slice<ChatRoom> findAllByUserIdAndIsDeletedFalse(Long customerId, PageRequest pageable);
}
