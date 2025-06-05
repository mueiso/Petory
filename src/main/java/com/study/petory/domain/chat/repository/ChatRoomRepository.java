package com.study.petory.domain.chat.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.study.petory.domain.chat.entity.ChatRoom;

public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {

	ChatRoom findByTradeBoardIdAndSellerId(Long tradeBoardId, Long sellerId);

	Page<ChatRoom> findAllByCustomerIdAndIsDeletedFalse(Long customerId, PageRequest pageable);
}
