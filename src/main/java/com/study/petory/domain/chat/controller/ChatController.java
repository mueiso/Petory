package com.study.petory.domain.chat.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.study.petory.common.response.CommonResponse;
import com.study.petory.common.security.CustomPrincipal;
import com.study.petory.domain.chat.dto.request.MessageSendRequestDto;
import com.study.petory.domain.chat.dto.response.ChatRoomCreateResponseDto;
import com.study.petory.domain.chat.dto.response.ChatRoomGetAllResponseDto;
import com.study.petory.domain.chat.dto.response.ChatRoomGetResponseDto;
import com.study.petory.domain.chat.entity.ChatMessage;
import com.study.petory.domain.chat.service.ChatService;
import com.study.petory.common.exception.enums.SuccessCode;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

	private final ChatService chatService;
	private final SimpMessagingTemplate messagingTemplate;

	/**
	 * 메시지 보내기
	 * @param requestDto 채팅방 아이디, 판매자 아이디, 보내려는 메시지
	 */
	@MessageMapping("/message")
	public void sendMessage(
		@AuthenticationPrincipal CustomPrincipal currentUser,
		@Valid MessageSendRequestDto requestDto
	) {
		ChatMessage message = chatService.createMessage(currentUser.getId(), requestDto);

		messagingTemplate.convertAndSend("/sub/room/" + requestDto.getChatRoomId(), message);
	}

	/**
	 * 채팅방 생성
	 * @param tradeBoardId 게시글 아이디
	 * @return 채팅방 아이디, 게시글 아이디, 구매.판매자 아이디
	 */
	@PostMapping({"/{tradeBoardId}"})
	public ResponseEntity<CommonResponse<ChatRoomCreateResponseDto>> createChatRoom(
		@AuthenticationPrincipal CustomPrincipal currentUser,
		@PathVariable Long tradeBoardId
	) {
		return CommonResponse.of(SuccessCode.CREATED, chatService.saveChatRoom(currentUser.getId(), tradeBoardId));
	}

	/**
	 * 채팅방 전체 조회
	 * @param pageable
	 * @return 채팅방 아이디, 상대방 아이디
	 */
	@GetMapping
	public ResponseEntity<CommonResponse<List<ChatRoomGetAllResponseDto>>> getAllChatRoom(
		@AuthenticationPrincipal CustomPrincipal currentUser,
		Pageable pageable
	) {
		return CommonResponse.of(SuccessCode.FOUND, chatService.findAllChatRoom(currentUser.getId(), pageable));
	}

	/**
	 * 채팅방 단건 조회
	 * @param chatRoomId 조회하려는 채팅방 아이디
	 * @return 게시판 아이디, 판매.구매자 아이디, 메시지 리스트
	 */
	@GetMapping("/{chatRoomId}")
	public ResponseEntity<CommonResponse<ChatRoomGetResponseDto>> getByChatRoomId(
		@AuthenticationPrincipal CustomPrincipal currentUser,
		@PathVariable String chatRoomId
	) {
		return CommonResponse.of(SuccessCode.FOUND, chatService.findChatRoomById(currentUser.getId(), chatRoomId));
	}

}