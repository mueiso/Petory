package com.study.petory.domain.chat.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RequestBody;

import com.study.petory.common.response.CommonResponse;
import com.study.petory.domain.chat.dto.request.ChatMessageSendRequestDto;
import com.study.petory.domain.chat.entity.ChatMessage;
import com.study.petory.domain.chat.service.ChatMessageService;
import com.study.petory.exception.enums.ErrorCode;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChatMessageController {

	private final ChatMessageService chatMessageService;
	private final SimpMessagingTemplate messagingTemplate;

	/**
	 * 메시지 보내기
	 * @param requestDto roomId, senderId, 보내려는 메시지
	 */
	@MessageMapping("/message")
	public void sendMessage(
		@Valid ChatMessageSendRequestDto requestDto
	) {
		ChatMessage chatMessage = chatMessageService.createChatMessage(requestDto);

		messagingTemplate.convertAndSend("/sub/room/" + requestDto.getChatRoomId(), chatMessage);
	}

	@MessageExceptionHandler(MethodArgumentNotValidException.class)
	public void messageValidException(MethodArgumentNotValidException e) {
		messagingTemplate.convertAndSend("/sub/errors", "잘못된 입력값이 존재합니다: " + e.getMessage());
	}
}
