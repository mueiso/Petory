package com.study.petory.domain.chat.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;

import com.study.petory.domain.chat.dto.response.ChatRoomCreateResponseDto;
import com.study.petory.domain.chat.dto.response.ChatRoomGetAllResponseDto;
import com.study.petory.domain.chat.dto.response.ChatRoomGetResponseDto;
import com.study.petory.domain.chat.entity.ChatMessage;
import com.study.petory.domain.chat.entity.ChatRoom;
import com.study.petory.domain.chat.repository.ChatMessageRepository;
import com.study.petory.domain.chat.repository.ChatRoomRepository;
import com.study.petory.domain.tradeBoard.entity.TradeBoard;
import com.study.petory.domain.tradeBoard.repository.TradeBoardRepository;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.repository.UserRepository;
import com.study.petory.exception.CustomException;
import com.study.petory.exception.enums.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService{

	private final ChatRoomRepository chatRoomRepository;
	private final TradeBoardRepository tradeBoardRepository;
	private final UserRepository userRepository;
	private final ChatMessageRepository chatMessageRepository;

	//채팅방 생성
	@Override
	public ChatRoomCreateResponseDto saveChatRoom(Long tradeBoardId) {

		//토큰 값으로 수정 예정
		User customer = userRepository.findById(2L)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		TradeBoard tradeBoard = tradeBoardRepository.findById(tradeBoardId)
			.orElseThrow(() -> new CustomException(ErrorCode.TRADE_BOARD_NOT_FOUND));

		User seller = userRepository.findById(tradeBoard.getUser().getId())
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		//해당 물품에 관한 채팅방이 존재하거나 삭제되지 않은 경우 예외처리
		ChatRoom existRoom = chatRoomRepository.findByTradeBoardIdAndSellerId(tradeBoardId, seller.getId());

		if (existRoom != null && !existRoom.isDeleted()) {
			throw new CustomException(ErrorCode.CHAT_ROOM_ALREADY_EXIST);
		}

		ChatRoom chatRoom = ChatRoom.builder()
			.sellerId(seller.getId())
			.customerId(customer.getId())
			.tradeBoardId(tradeBoardId)
			.build();

		ChatRoom savedCharRoom = chatRoomRepository.save(chatRoom);

		return new ChatRoomCreateResponseDto(savedCharRoom);
	}

	//로그인 한 사용자 채팅방 전체 조회
	@Override
	public Slice<ChatRoomGetAllResponseDto> findAllChatRoom() {

		//수정 예정
		User user = userRepository.findById(2L)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		PageRequest pageable = PageRequest.of(0, 15);

		Slice<ChatRoom> chatRooms = chatRoomRepository.findAllByUserIdAndIsDeletedFalse(user.getId(), pageable);

		return chatRooms.map(chatRoom -> {
			Long opponentUserId =
				chatRoom.isEqualSeller(user.getId()) ? chatRoom.getCustomerId() : chatRoom.getSellerId();

			User opponentUSer = userRepository.findById(opponentUserId)
				.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

			return new ChatRoomGetAllResponseDto(chatRoom, opponentUSer.getNickname());
		});
	}

	//채팅방 단건 조회
	@Override
	public ChatRoomGetResponseDto findByChatRoomId(String chatRoomId) {

		ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
			.orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));

		List<ChatMessage> messages = chatMessageRepository.findAllByChatRoomId(chatRoomId);

		return new ChatRoomGetResponseDto(chatRoom, messages);
	}

	//채팅방 삭제(소프트 딜리트)
	@Override
	public void deleteChatRoom(String chatRoomId) {

		//채팅방 유저 검증 로직 추가 예정

		ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
			.orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));

		chatRoom.deactivateChatRoom();
		chatRoomRepository.save(chatRoom);
	}
}
