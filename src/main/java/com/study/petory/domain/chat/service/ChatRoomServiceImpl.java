package com.study.petory.domain.chat.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.study.petory.domain.chat.dto.response.ChatRoomCreateResponseDto;
import com.study.petory.domain.chat.dto.response.ChatRoomGetResponseDto;
import com.study.petory.domain.chat.entity.ChatRoom;
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

	//채팅방 생성
	@Override
	public ChatRoomCreateResponseDto saveChatRoom(Long tradeBoardId) {

		//토큰 값으로 수정 예정
		User customer = userRepository.findById(1L)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		TradeBoard tradeBoard = tradeBoardRepository.findById(tradeBoardId)
			.orElseThrow(() -> new CustomException(ErrorCode.TRADE_BOARD_NOT_FOUND));

		User seller = userRepository.findById(tradeBoard.getUser().getId())
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		//해당 물품에 관한 채팅방이 존재할 경우 예외처리
		if (chatRoomRepository.findByTradeBoardIdAndSellerId(tradeBoardId, seller.getId()) != null) {
			throw new CustomException(ErrorCode.CHAT_ROOM_ALREADY_EXIST);
		}

		ChatRoom chatRoom = ChatRoom.builder()
			.sellerId(seller.getId())
			.customerId(customer.getId())
			.tradeBoardId(tradeBoardId)
			.tradeBoardTitle(tradeBoard.getTitle())
			.tradeBoardUrl("/trade-boards/" + tradeBoard.getId())
			.build();

		ChatRoom savedCharRoom = chatRoomRepository.save(chatRoom);

		return new ChatRoomCreateResponseDto(savedCharRoom, tradeBoard);
	}

	//로그인 한 사용자 채팅방 전체 조회
	@Override
	public Page<ChatRoomGetResponseDto> findAllChatRoom(int page) {

		//수정 예정
		User customer = userRepository.findById(1L)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		int adjustPage = page > 0 ? page - 1 : 0;
		PageRequest pageable = PageRequest.of(adjustPage, 10);

		Page<ChatRoom> chatRooms = chatRoomRepository.findAllByCustomerIdAndDeletedFalse(customer.getId(), pageable);

		return chatRooms.map(ChatRoomGetResponseDto::new);
	}

	//채팅방 삭제(소프트 딜리트)
	@Override
	public void deleteChatRoom(String chatRoomId) {

		ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
			.orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));

		chatRoom.deactivateChatRoom();
	}
}
