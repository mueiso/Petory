package com.study.petory.domain.chat.service;

import org.springframework.stereotype.Service;

import com.study.petory.domain.chat.dto.response.ChatRoomCreateResponseDto;
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

	@Override
	public ChatRoomCreateResponseDto saveChatRoom(Long tradeBoardId) {

		//토큰 값으로 수정 예정
		User customer = userRepository.findById(1L)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		TradeBoard tradeBoard = tradeBoardRepository.findById(tradeBoardId)
			.orElseThrow(() -> new CustomException(ErrorCode.TRADE_BOARD_NOT_FOUND));

		User seller = userRepository.findById(tradeBoard.getUser().getId())
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		if (chatRoomRepository.findByTradeBoardIdAndSellerId(tradeBoardId, seller.getId()) != null) {
			throw new CustomException(ErrorCode.CHAT_ROOM_ALREADY_EXIST);
		}

		ChatRoom chatRoom = ChatRoom.builder()
			.sellerId(seller.getId())
			.customerId(customer.getId())
			.tradeBoardId(tradeBoardId)
			.tradeBoardUrl("/trade-boards/" + tradeBoard.getId())
			.build();

		ChatRoom savedCharRoom = chatRoomRepository.save(chatRoom);

		return new ChatRoomCreateResponseDto(savedCharRoom, tradeBoard);
	}
}
