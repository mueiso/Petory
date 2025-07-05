package com.study.petory.domain.chat.repository;

import static com.mysema.commons.lang.Assert.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import com.study.petory.domain.chat.entity.ChatRoom;

@DataMongoTest
class ChatRepositoryTest {

	@Autowired
	private ChatRepository chatRepository;

	@BeforeEach
	void setUp() {
		chatRepository.deleteAll();
	}

	@Test
	void 유저_아이디로_채팅방_찾기에_성공한다() {
		//given
		PageRequest pageable = PageRequest.of(0, 10);

		ChatRoom chatRoom = ChatRoom.builder()
			.tradeBoardId(1L)
			.sellerId(1L)
			.customerId(2L)
			.build();
		ReflectionTestUtils.setField(chatRoom, "id", new ObjectId());
		chatRepository.save(chatRoom);

		//when
		List<ChatRoom> chatRooms = chatRepository.findChatRoomsByUserId(1L, pageable);

		//then
		assertEquals(1, chatRooms.size());
		assertEquals(1L, chatRooms.get(0).getTradeBoardId());
		assertEquals(1L, chatRooms.get(0).getSellerId());
		assertEquals(2L, chatRooms.get(0).getCustomerId());
		assertTrue(chatRooms.get(0).isSellerExist());
		assertTrue(chatRooms.get(0).isCustomerExist());
	}
}