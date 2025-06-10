package com.study.petory.domain.chat.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.study.petory.common.response.CommonResponse;
import com.study.petory.domain.chat.dto.request.MessageSendRequestDto;
import com.study.petory.domain.chat.dto.response.ChatRoomCreateResponseDto;
import com.study.petory.domain.chat.dto.response.ChatRoomGetAllResponseDto;
import com.study.petory.domain.chat.dto.response.ChatRoomGetResponseDto;
import com.study.petory.domain.chat.entity.ChatMessage;
import com.study.petory.domain.chat.service.ChatService;
import com.study.petory.exception.enums.SuccessCode;

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

	@PostMapping({"/{tradeBoardId}"})
	public ResponseEntity<CommonResponse<ChatRoomCreateResponseDto>> createChatRoom(
		@PathVariable Long tradeBoardId
	) {
		return CommonResponse.of(SuccessCode.CREATED, chatService.saveChatRoom(tradeBoardId));
	}

	@GetMapping
	public ResponseEntity<CommonResponse<List<ChatRoomGetAllResponseDto>>> getAllChatRoom(
		@RequestParam(defaultValue = "1") int page
	) {
		return CommonResponse.of(SuccessCode.FOUND, chatService.findAllChatRoom(page));
	}

	@GetMapping("/{chatRoomId}")
	public ResponseEntity<CommonResponse<ChatRoomGetResponseDto>> getByChatRoomId(
		@PathVariable String chatRoomId
	) {
		return CommonResponse.of(SuccessCode.FOUND, chatService.findChatRoomById(chatRoomId));
	}

}