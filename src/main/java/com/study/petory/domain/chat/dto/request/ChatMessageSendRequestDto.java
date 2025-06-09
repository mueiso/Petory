package com.study.petory.domain.chat.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatMessageSendRequestDto {

	@NotBlank(message = "원하는 채팅방을 입력해주세요.")
	private final String chatRoomId;

	@NotNull(message = "전달하려는 사용자를 입력해주세요.")
	private final Long senderId;

	@NotBlank(message = "전달하려는 메시지를 입력해주세요.")
	private final String message;
}
