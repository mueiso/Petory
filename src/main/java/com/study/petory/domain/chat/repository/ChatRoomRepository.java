package com.study.petory.domain.chat.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.study.petory.domain.chat.entity.ChatRoom;

public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {

	ChatRoom findByTradeBoardIdAndSellerId(Long tradeBoardId, Long sellerId);

	@Query("{$or: [{'sellerId': ?0}, {'customerId': ?0}], 'isDeleted': false}")
	Slice<ChatRoom> findAllByUserIdAndIsDeletedFalse(Long customerId, PageRequest pageable);
}
