package com.study.petory.domain.chat.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import com.study.petory.domain.chat.dto.response.ChatRoomCreateResponseDto;
import com.study.petory.domain.chat.dto.response.ChatRoomGetAllResponseDto;
import com.study.petory.domain.chat.dto.response.ChatRoomGetResponseDto;
import com.study.petory.domain.chat.entity.ChatMessage;
import com.study.petory.domain.chat.entity.ChatRoom;
import com.study.petory.domain.chat.repository.ChatAggregateRepository;
import com.study.petory.domain.chat.repository.ChatRepository;
import com.study.petory.domain.tradeBoard.entity.TradeBoard;
import com.study.petory.domain.tradeBoard.entity.TradeCategory;
import com.study.petory.domain.tradeBoard.repository.TradeBoardRepository;
import com.study.petory.domain.user.entity.Role;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.entity.UserPrivateInfo;
import com.study.petory.domain.user.entity.UserRole;
import com.study.petory.domain.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class ChatServiceImplTest {

	@Mock
	private ChatRepository chatRepository;

	@Mock
	private ChatAggregateRepository chatAggregateRepository;

	@Mock
	private TradeBoardRepository tradeBoardRepository;

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private ChatServiceImpl chatService;

	private User user1;
	private User user2;
	private TradeBoard tradeBoard;

	@BeforeEach
	void setUp() {
		List<UserRole> userRoles = new ArrayList<>();
		userRoles.add(new UserRole(Role.USER));

		UserPrivateInfo userInfo1 = new UserPrivateInfo("authId1", "name1", "01012341234");
		this.user1 = new User("nickname1", "test1@test.com", userInfo1, userRoles);
		ReflectionTestUtils.setField(user1, "id", 1L);

		UserPrivateInfo userInfo2 = new UserPrivateInfo("authId2", "name2", "01098765432");
		this.user2 = new User("nickname2", "test2@test.com", userInfo2, userRoles);
		ReflectionTestUtils.setField(user2, "id", 2L);

		tradeBoard = TradeBoard.builder()
			.category(TradeCategory.HEALTH)
			.title("title")
			.content("content")
			.price(2000)
			.user(user2)
			.build();
	}

	@Test
	void 채팅방_생성에_성공한다() {
		// given
		Long userId = 1L;
		Long tradeBoardId = 1L;
		ReflectionTestUtils.setField(tradeBoard, "id", tradeBoardId);

		when(tradeBoardRepository.findById(tradeBoardId)).thenReturn(Optional.of(tradeBoard));

		when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));

		when(chatRepository.save(any(ChatRoom.class)))
			.thenAnswer(invocation -> {
				ChatRoom chatRoom = invocation.getArgument(0);
				ReflectionTestUtils.setField(chatRoom, "id", new ObjectId());
				return chatRoom;
			});

		// when
		ChatRoomCreateResponseDto responseDto = chatService.saveChatRoom(userId, tradeBoardId);

		// then
		assertThat(responseDto).isNotNull();
		assertThat(responseDto.getTradeBoardId()).isEqualTo(tradeBoardId);
		assertThat(responseDto.getSellerId()).isEqualTo(user2.getId());
		assertThat(responseDto.getCustomerId()).isEqualTo(user1.getId());
		assertThat(responseDto.getChatRoomId()).isNotNull();
	}

	@Test
	void 채팅방_전체_조회_성공() {
		// given
		PageRequest pageable = PageRequest.of(0, 10);
		Long loginUserId = 2L;

		List<ChatRoom> chatRooms = new ArrayList<>();
		ChatRoom chatRoom1 = ChatRoom.builder()
			.tradeBoardId(1L)
			.sellerId(1L)
			.customerId(2L)
			.build();
		ReflectionTestUtils.setField(chatRoom1, "id", new ObjectId());
		chatRooms.add(chatRoom1);

		ChatRoom chatRoom2 = ChatRoom.builder()
			.tradeBoardId(2L)
			.sellerId(3L)
			.customerId(2L)
			.build();
		ReflectionTestUtils.setField(chatRoom2, "id", new ObjectId());
		chatRooms.add(chatRoom2);

		when(chatAggregateRepository.findChatRoomsByUserId(loginUserId, pageable)).thenReturn(chatRooms);

		// when
		List<ChatRoomGetAllResponseDto> responseDto = chatService.findAllChatRoom(loginUserId, pageable);

		// then
		assertThat(responseDto).hasSize(2);

		assertThat(responseDto.get(0).getChatRoomId()).isEqualTo(chatRoom1.getId().toHexString());
		assertThat(responseDto.get(0).getOpponentId()).isEqualTo(1L); // sellerId가 상대방

		assertThat(responseDto.get(1).getChatRoomId()).isEqualTo(chatRoom2.getId().toHexString());
		assertThat(responseDto.get(1).getOpponentId()).isEqualTo(3L); // sellerId가 상대방
	}

	@Test
	void 채팅방_단건_조회_성공() {
		// given
		String chatRoomId = new ObjectId().toHexString();
		Long userId = 1L;

		// 채팅방에 메시지를 추가
		ChatMessage message = ChatMessage.builder()
			.senderId(2L)
			.message("안녕")
			.build();

		ChatRoom chatRoom = ChatRoom.builder()
			.tradeBoardId(1L)
			.sellerId(1L)
			.customerId(2L)
			.build();
		ReflectionTestUtils.setField(chatRoom, "id", new ObjectId(chatRoomId));
		chatRoom.addMessage(message);

		when(chatRepository.findById(new ObjectId(chatRoomId)))
			.thenReturn(Optional.of(chatRoom));

		// when
		ChatRoomGetResponseDto responseDto = chatService.findChatRoomById(userId, chatRoomId);

		// then
		assertThat(responseDto).isNotNull();
		assertThat(responseDto.getTradeBoardId()).isEqualTo(chatRoom.getTradeBoardId());
		assertThat(responseDto.getCustomerId()).isEqualTo(chatRoom.getCustomerId());
		assertThat(responseDto.getSellerId()).isEqualTo(chatRoom.getSellerId());

		assertThat(responseDto.getMessages()).hasSize(1);
		assertThat(responseDto.getMessages().get(0).getMessage()).isEqualTo("안녕");
	}
}