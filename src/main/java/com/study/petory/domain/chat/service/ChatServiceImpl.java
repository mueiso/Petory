package com.study.petory.domain.chat.service;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import com.study.petory.domain.chat.dto.request.MessageSendRequestDto;
import com.study.petory.domain.chat.entity.ChatMessage;
import com.study.petory.domain.chat.entity.ChatRoom;
import com.study.petory.domain.chat.repository.ChatRepository;
import com.study.petory.exception.CustomException;
import com.study.petory.exception.enums.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService{

	private final ChatRepository chatRepository;

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
}
