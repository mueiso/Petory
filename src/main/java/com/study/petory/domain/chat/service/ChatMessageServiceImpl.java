package com.study.petory.domain.chat.service;

import org.springframework.stereotype.Service;

import com.study.petory.domain.chat.dto.request.ChatMessageSendRequestDto;
import com.study.petory.domain.chat.entity.ChatMessage;
import com.study.petory.domain.chat.entity.ChatRoom;
import com.study.petory.domain.chat.repository.ChatRoomRepository;
import com.study.petory.exception.CustomException;
import com.study.petory.exception.enums.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatMessageServiceImpl implements ChatMessageService {

	private final ChatRoomRepository chatRoomRepository;

	@Override
	public ChatMessage createChatMessage(ChatMessageSendRequestDto requestDto) {

		ChatRoom chatRoom = chatRoomRepository.findById(requestDto.getChatRoomId())
			.orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));

		ChatMessage chatMessage = ChatMessage.builder()
			.chatRoomId(requestDto.getChatRoomId())
			.senderId(requestDto.getSenderId())
			.message(requestDto.getMessage())
			.build();

		chatRoom.updateLastMessage(requestDto.getMessage());
		chatRoomRepository.save(chatRoom);

		return chatMessage;
	}
}
