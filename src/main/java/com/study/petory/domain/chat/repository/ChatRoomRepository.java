package com.study.petory.domain.chat.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.study.petory.domain.chat.entity.ChatRoom;

public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {

	ChatRoom findByTradeBoardIdAndSellerId(Long tradeBoardId, Long sellerId);

	Slice<ChatRoom> findAllByCustomerIdAndIsDeletedFalse(Long customerId, PageRequest pageable);
}
