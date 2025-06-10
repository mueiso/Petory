package com.study.petory.domain.chat.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.study.petory.common.response.CommonResponse;
import com.study.petory.domain.chat.dto.request.MessageSendRequestDto;
import com.study.petory.domain.chat.dto.response.ChatRoomCreateResponseDto;
import com.study.petory.domain.chat.entity.ChatMessage;
import com.study.petory.domain.chat.service.ChatService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("chat")
@RequiredArgsConstructor
public class ChatController {

	private final ChatService chatService;
	private final SimpMessagingTemplate messagingTemplate;

	@MessageMapping("/message")
	public void sendMessage(
		@Valid MessageSendRequestDto requestDto
	) {
		ChatMessage message = chatService.createMessage(requestDto);

		messagingTemplate.convertAndSend("/sub/room/" + requestDto.getChatRoomId(), message);
	}


}