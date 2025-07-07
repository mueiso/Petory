package com.study.petory.domain.chat.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.study.petory.domain.chat.entity.ChatRoom;

public interface ChatRepository extends MongoRepository<ChatRoom, ObjectId>, ChatAggregateRepository {

}
