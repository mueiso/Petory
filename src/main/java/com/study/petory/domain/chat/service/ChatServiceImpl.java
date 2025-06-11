package com.study.petory.domain.chat.service;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import com.study.petory.domain.chat.dto.request.MessageSendRequestDto;
import com.study.petory.domain.chat.dto.response.ChatRoomCreateResponseDto;
import com.study.petory.domain.chat.dto.response.ChatRoomGetAllResponseDto;
import com.study.petory.domain.chat.dto.response.ChatRoomGetResponseDto;
import com.study.petory.domain.chat.entity.ChatMessage;
import com.study.petory.domain.chat.entity.ChatRoom;
import com.study.petory.domain.chat.repository.ChatAggregateRepository;
import com.study.petory.domain.chat.repository.ChatRepository;
import com.study.petory.domain.tradeBoard.entity.TradeBoard;
import com.study.petory.domain.tradeBoard.repository.TradeBoardRepository;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.repository.UserRepository;
import com.study.petory.exception.CustomException;
import com.study.petory.exception.enums.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService{

	private final ChatRepository chatRepository;
	private final UserRepository userRepository;
	private final TradeBoardRepository tradeBoardRepository;
	private final ChatAggregateRepository aggregateRepository;

	//사용하지 않으면 삭제 예정
	private User findUserById(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
	}

	//메시지 보내기
	@Override
	public ChatMessage createMessage(MessageSendRequestDto requestDto) {

		ChatRoom chatRoom = chatRepository.findById(new ObjectId(requestDto.getChatRoomId()))
			.orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));

		ChatMessage message = ChatMessage.builder()
			.senderId(requestDto.getSenderId())
			.message(requestDto.getMessage())
			.build();

		chatRoom.addMessage(message);
		chatRepository.save(chatRoom);

		return message;
	}

	//채팅방 생성
	@Override
	public ChatRoomCreateResponseDto saveChatRoom(Long tradeBoardId) {

		TradeBoard tradeBoard = tradeBoardRepository.findById(tradeBoardId)
			.orElseThrow(() -> new CustomException(ErrorCode.TRADE_BOARD_NOT_FOUND));

		ChatRoom chatRoom = ChatRoom.builder()
			.tradeBoardId(tradeBoardId)
			.sellerId(tradeBoard.getUser().getId())
			.customerId(2L)
			.build();

		chatRepository.save(chatRoom);

		return new ChatRoomCreateResponseDto(chatRoom);
	}

	//채팅방 전체 조회
	@Override
	public List<ChatRoomGetAllResponseDto> findAllChatRoom(int page) {

		int adjustPage = (page > 0) ? page - 1 : 0;

		List<ChatRoom> chatRooms = aggregateRepository.findChatRoomsByUserId(2L, adjustPage);

		return chatRooms.stream()
			.map(chatRoom -> new ChatRoomGetAllResponseDto(chatRoom, 2L))
			.toList();
	}

	//채팅방 단건 조회
	@Override
	public ChatRoomGetResponseDto findChatRoomById(String chatRoomId) {

		ChatRoom chatRoom = chatRepository.findById(new ObjectId(chatRoomId))
			.orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));

		return new ChatRoomGetResponseDto(chatRoom);
	}
}
