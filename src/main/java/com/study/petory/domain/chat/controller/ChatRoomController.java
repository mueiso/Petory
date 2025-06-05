package com.study.petory.domain.chat.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.study.petory.common.response.CommonResponse;
import com.study.petory.domain.chat.dto.response.ChatRoomCreateResponseDto;
import com.study.petory.domain.chat.dto.response.ChatRoomGetResponseDto;
import com.study.petory.domain.chat.service.ChatRoomService;
import com.study.petory.exception.enums.SuccessCode;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/chat-room")
@RequiredArgsConstructor
public class ChatRoomController {

	private final ChatRoomService chatRoomService;

	/**
	 *
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
	 *
	 * @param page 조회할 페이지
	 * @return 로그인한 사용자의 채팅방
	 */
	@GetMapping
	public ResponseEntity<CommonResponse<Page<ChatRoomGetResponseDto>>> getAllCharRoom(
		@RequestParam(defaultValue = "1") int page
	) {
		return CommonResponse.of(SuccessCode.FOUND, chatRoomService.findAllChatRoom(page));
	}
}
