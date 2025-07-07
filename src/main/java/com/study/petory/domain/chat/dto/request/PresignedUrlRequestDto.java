package com.study.petory.domain.chat.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PresignedUrlRequestDto {

	private final String chatRoomId;

	private final String filename;

	private final String contentType;

}
