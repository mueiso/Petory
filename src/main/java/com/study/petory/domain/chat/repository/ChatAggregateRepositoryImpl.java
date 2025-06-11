package com.study.petory.domain.chat.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import com.study.petory.domain.chat.entity.ChatRoom;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ChatAggregateRepositoryImpl implements ChatAggregateRepository{

	private final MongoTemplate mongoTemplate;

	@Override
	public List<ChatRoom> findChatRoomsByUserId(Long userId, int page) {

		Criteria criteria = new Criteria().orOperator(
			Criteria.where("sellerId").is(userId),
			Criteria.where("customerId").is(userId)
		);

		Aggregation aggregation = Aggregation.newAggregation(
			Aggregation.match(criteria),
			Aggregation.sort(Sort.by(Sort.Direction.DESC, "lastMessageDate")),
			Aggregation.skip((long)page * 10),
			Aggregation.limit(10)
		);

		return mongoTemplate.aggregate(aggregation, "chatroom", ChatRoom.class).getMappedResults();
	}
}
