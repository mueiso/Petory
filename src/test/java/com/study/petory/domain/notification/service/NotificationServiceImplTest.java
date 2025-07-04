package com.study.petory.domain.notification.service;

import static com.study.petory.domain.user.entity.QUser.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import com.study.petory.domain.notification.dto.response.NotificationGetResponseDto;
import com.study.petory.domain.notification.entity.Notification;
import com.study.petory.domain.notification.repository.NotificationRepository;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.service.UserService;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

	@Mock
	private NotificationRepository notificationRepository;

	@Mock
	private UserService userService;

	@InjectMocks
	private NotificationServiceImpl notificationService;

	private User user = new User(1L);

	@Test
	void 알림_조회에_성공한다() {
		// given
		Pageable pageable = PageRequest.of(0, 10);

		Notification notification = Notification.builder()
			.user(user)
			.content("오늘의 질문이 도착했습니다.")
			.build();
		ReflectionTestUtils.setField(notification, "id", 1L);

		Page<Notification> page = new PageImpl<>(List.of(notification), pageable, 1);

		when(userService.findUserById(1L)).thenReturn(user);
		when(notificationRepository.findByUserId(1L, pageable)).thenReturn(page);

		// when
		Page<NotificationGetResponseDto> notifications = notificationService.findNotificationByUser(1L, pageable);

		// then
		assertThat(notifications).hasSize(1);
		assertThat(notifications.getContent().get(0).getContent()).isEqualTo("오늘의 질문이 도착했습니다.");
	}

	@Test
	void 알림_삭제에_성공한다() {
		//given
		Notification notification = Notification.builder()
			.user(user)
			.content("삭제될 알림")
			.build();
		ReflectionTestUtils.setField(notification, "id", 1L);

		when(userService.findUserById(1L)).thenReturn(user);
		when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));

		//when
		notificationService.deleteNotification(1L, 1L);

		//then
		verify(notificationRepository).delete(notification);
	}
}