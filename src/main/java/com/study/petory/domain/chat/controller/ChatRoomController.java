package com.study.petory.domain.chat.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.study.petory.common.response.CommonResponse;
import com.study.petory.domain.chat.dto.response.ChatRoomCreateResponseDto;
import com.study.petory.domain.chat.service.ChatRoomService;
import com.study.petory.exception.enums.SuccessCode;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/chat-room")
@RequiredArgsConstructor
public class ChatRoomController {

	private final ChatRoomService chatRoomService;

	@PostMapping("/{tradeBoardId}")
	public ResponseEntity<CommonResponse<ChatRoomCreateResponseDto>> createChatRoom(@PathVariable Long tradeBoardId) {

		return CommonResponse.of(SuccessCode.CREATED, chatRoomService.saveChatRoom(tradeBoardId));
	}
}
