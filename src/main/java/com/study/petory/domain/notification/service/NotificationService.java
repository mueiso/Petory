package com.study.petory.domain.notification.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.study.petory.domain.notification.dto.response.NotificationGetResponseDto;

public interface NotificationService {

	Page<NotificationGetResponseDto> findNotificationByUser(Long userId, Pageable pageable);

	void deleteNotification(Long userId, Long notificationId);
}
