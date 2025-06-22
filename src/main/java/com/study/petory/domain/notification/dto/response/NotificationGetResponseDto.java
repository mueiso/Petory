package com.study.petory.domain.notification.dto.response;

import java.time.LocalDateTime;

import com.study.petory.domain.notification.entity.Notification;

import lombok.Getter;

@Getter
public class NotificationGetResponseDto {

	private final String content;

	private final LocalDateTime createdAt;

	public NotificationGetResponseDto(Notification notification) {
		this.content = notification.getContent();
		this.createdAt = notification.getCreatedAt();
	}
}
