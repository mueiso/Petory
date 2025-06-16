package com.study.petory.domain.chat.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
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
	public List<ChatRoom> findChatRoomsByUserId(Long userId, Pageable pageable) {

		//조회 기준 등록 QueryDSL BooleanBuilder와 유사
		Criteria criteria = new Criteria().orOperator(
			Criteria.where("sellerId").is(userId).and("sellerExist").is(true),
			Criteria.where("customerId").is(userId).and("customerExist").is(true)
		);

		Aggregation aggregation = Aggregation.newAggregation(
			Aggregation.match(criteria),
			Aggregation.sort(Sort.by(Sort.Direction.DESC, "lastMessageDate")),
			Aggregation.skip(pageable.getOffset()),
			Aggregation.limit(pageable.getPageSize())
		);

		return mongoTemplate.aggregate(aggregation, "chatroom", ChatRoom.class).getMappedResults();
	}
}
