package com.study.petory.domain.chat.controller;

import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.study.petory.common.exception.enums.SuccessCode;
import com.study.petory.common.response.CommonResponse;
import com.study.petory.domain.chat.dto.response.ChatRoomAllGetResponseDto;
import com.study.petory.domain.chat.dto.response.ChatRoomCreateResponseDto;
import com.study.petory.domain.chat.dto.response.ChatRoomGetResponseDto;
import com.study.petory.domain.chat.service.ChatRoomService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/chat-room")
@RequiredArgsConstructor
public class ChatRoomController {

	private final ChatRoomService chatRoomService;

	/**
	 * 채팅방 생성
	 * @param tradeBoardId 거래하려는 게시판 id
	 * @return 거래게시판 url, title, 채팅에 참여한 유저정보
	 */
	@PostMapping("/{tradeBoardId}")
	public ResponseEntity<CommonResponse<ChatRoomCreateResponseDto>> createChatRoom(
		@PathVariable Long tradeBoardId
	) {
		return CommonResponse.of(SuccessCode.CREATED, chatRoomService.saveChatRoom(tradeBoardId));
	}

	/**
	 * 채팅방 조회
	 * @return 로그인한 사용자의 채팅방
	 */
	@GetMapping
	public ResponseEntity<CommonResponse<Slice<ChatRoomAllGetResponseDto>>> getAllCharRoom(
	) {
		return CommonResponse.of(SuccessCode.FOUND, chatRoomService.findAllChatRoom());
	}

	/**
	 * 채팅방 단건 조회
	 * @param chatRoomId 조회하려는 채팅방 아이디
	 * @return 채팅 내역 및 거래 물품
	 */
	@GetMapping("/{chatRoomId}")
	public ResponseEntity<CommonResponse<ChatRoomGetResponseDto>> getByChatRoomId(
		@PathVariable String chatRoomId
	) {
		return CommonResponse.of(SuccessCode.FOUND, chatRoomService.findByChatRoomId(chatRoomId));
	}

	/**
	 * 채팅방 삭제(소프트 딜리트 구현)
	 * @param chatRoomId 삭제하려는 채팅방 아이디
	 * @return 삭제 확인 메시지
	 */
	@DeleteMapping("/{chatRoomId}")
	public ResponseEntity<CommonResponse<Void>> deleteChatRoom(
		@PathVariable String chatRoomId
	) {
		chatRoomService.deleteChatRoom(chatRoomId);
		return CommonResponse.of(SuccessCode.DELETED);
	}
}
