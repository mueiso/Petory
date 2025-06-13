package com.study.petory.domain.chat.service;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
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
import com.study.petory.common.exception.CustomException;
import com.study.petory.common.exception.enums.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService{

	private final ChatRepository chatRepository;
	private final UserRepository userRepository;
	private final TradeBoardRepository tradeBoardRepository;
	private final ChatAggregateRepository aggregateRepository;

	//사용하지 않으면 삭제 예정
	public User findUser(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
	}

	public ChatRoom findChatRoom(String chatRoomId) {
		return chatRepository.findById(new ObjectId(chatRoomId))
			.orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));
	}

	//메시지 보내기
	@Override
	public ChatMessage createMessage(Long userId, MessageSendRequestDto requestDto) {

		ChatRoom chatRoom = findChatRoom(requestDto.getChatRoomId());
		User user = findUser(userId);

		if (!chatRoom.isMember(user.getId())) {
			throw new CustomException(ErrorCode.FORBIDDEN);
		}

		ChatMessage message = ChatMessage.builder()
			.senderId(user.getId())
			.senderNickname(user.getNickname())
			.message(requestDto.getMessage())
			.build();

		chatRoom.addMessage(message);
		chatRepository.save(chatRoom);

		return message;
	}

	//채팅방 생성
	@Override
	public ChatRoomCreateResponseDto saveChatRoom(Long userId, Long tradeBoardId) {

		TradeBoard tradeBoard = tradeBoardRepository.findById(tradeBoardId)
			.orElseThrow(() -> new CustomException(ErrorCode.TRADE_BOARD_NOT_FOUND));

		findUser(userId);

		ChatRoom chatRoom = ChatRoom.builder()
			.tradeBoardId(tradeBoardId)
			.sellerId(tradeBoard.getUserId())
			.customerId(userId)
			.build();

		chatRepository.save(chatRoom);

		return new ChatRoomCreateResponseDto(chatRoom);
	}

	//채팅방 전체 조회
	@Override
	public List<ChatRoomGetAllResponseDto> findAllChatRoom(Long userId, Pageable pageable) {

		List<ChatRoom> chatRooms = aggregateRepository.findChatRoomsByUserId(userId, pageable);

		return chatRooms.stream()
			.map(chatRoom -> new ChatRoomGetAllResponseDto(chatRoom, userId))
			.toList();
	}

	//채팅방 단건 조회
	@Override
	public ChatRoomGetResponseDto findChatRoomById(Long userId, String chatRoomId) {

		ChatRoom chatRoom = findChatRoom(chatRoomId);

		if (chatRoom.isMember(userId)) {
			throw new CustomException(ErrorCode.FORBIDDEN);
		}

		return new ChatRoomGetResponseDto(chatRoom);
	}

	//채팅방 나가기
	@Override
	public void leaveChatRoomById(Long userId, String chatRoomId) {

		ChatRoom chatRoom = findChatRoom(chatRoomId);

		if (chatRoom.isMember(userId)) {
			throw new CustomException(ErrorCode.FORBIDDEN);
		}

		chatRoom.leaveChatRoom(userId);
		chatRepository.save(chatRoom);
	}

}
