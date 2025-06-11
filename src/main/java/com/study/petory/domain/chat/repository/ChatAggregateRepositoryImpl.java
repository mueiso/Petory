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

		//조회 기준 등록 QueryDSL BooleanBuilder와 유사
		Criteria criteria = new Criteria().orOperator(
			Criteria.where("sellerId").is(userId),
			Criteria.where("customerId").is(userId)
		);

		Aggregation aggregation = Aggregation.newAggregation(
			Aggregation.match(criteria), //criteria에 해당되는 데이터 필터링
			Aggregation.sort(Sort.by(Sort.Direction.DESC, "lastMessageDate")), //채팅의 마지막 날짜를 기준으로 채팅방 조회
			Aggregation.skip((long)page * 10), //조회하려는 페이지 이전의 데이터를 건너뛰고 조회
			Aggregation.limit(10) //조회하려는 크기
		);

		//mongoTemplate을 통해 특정 컬렉션을 aggregation에 지정한 형태로 조회 후 매핑
		return mongoTemplate.aggregate(aggregation, "chatroom", ChatRoom.class).getMappedResults();
	}
}
