package com.study.petory.domain.notification.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.study.petory.common.exception.CustomException;
import com.study.petory.common.exception.enums.ErrorCode;
import com.study.petory.domain.notification.dto.response.NotificationGetResponseDto;
import com.study.petory.domain.notification.entity.Notification;
import com.study.petory.domain.notification.repository.NotificationQueryRepository;
import com.study.petory.domain.notification.repository.NotificationRepository;
import com.study.petory.domain.user.entity.Role;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.repository.UserRepository;
import com.study.petory.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService{

	private final NotificationRepository notificationRepository;
	private final NotificationQueryRepository notificationQueryRepository;
	private final UserService userService;

	@Override
	public Page<NotificationGetResponseDto> findNotificationByUser(Long userId, Pageable pageable) {

		User user = userService.getUserById(userId);

		return notificationQueryRepository.findByUserId(userId, pageable)
			.map(NotificationGetResponseDto::new);
	}

	@Override
	public void deleteNotification(Long userId, Long notificationId) {

		User user = userService.getUserById(userId);

		Notification notification = notificationRepository.findById(notificationId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOTIFICATION_NOT_FOUND));

		if (!userId.equals(notification.getUserId()) && !user.hasRole(Role.ADMIN)) {
			throw new CustomException(ErrorCode.FORBIDDEN);
		}

		notificationRepository.delete(notification);
	}
}
