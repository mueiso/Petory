package com.study.petory.domain.notification.repository;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import com.study.petory.common.config.QueryDSLConfig;
import com.study.petory.domain.notification.entity.Notification;
import com.study.petory.domain.user.entity.Role;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.entity.UserPrivateInfo;
import com.study.petory.domain.user.entity.UserRole;
import com.study.petory.domain.user.repository.UserRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(QueryDSLConfig.class)
@ActiveProfiles("test")
class NotificationRepositoryTest {

	@Autowired
	private NotificationRepository notificationRepository;

	@Autowired
	private UserRepository userRepository;

	private Long userId;

	private final UserPrivateInfo userInfo = new UserPrivateInfo(
		"1L",
		"홍길동",
		"000-0000-0000"
	);

	private final List<UserRole> userRole = new ArrayList<>(
		List.of(new UserRole(Role.USER))
	);

	private final User user = User.builder()
		.email("test@email.com")
		.nickname("길동이")
		.userPrivateInfo(userInfo)
		.userRole(userRole)
		.build();

	@BeforeEach
	public void setUser() {
		userRepository.save(user);
		userId = user.getId();
	}

	@Test
	void 알림_조회에_성공한다() {
		//given
		PageRequest pageable = PageRequest.of(0, 10);
		LocalDateTime createTime = LocalDateTime.of(2025, 1, 1, 0, 0);

		Notification notification = Notification.builder()
			.user(user)
			.content("test")
			.build();
		ReflectionTestUtils.setField(notification, "createdAt", createTime);

		Notification savedNotification = notificationRepository.save(notification);

		//when
		Page<Notification> findedNotification = notificationRepository.findByUserId(userId, pageable);

		//then
		assertThat(findedNotification.getContent()).hasSize(1);
		assertThat(findedNotification.getContent().get(0).getContent()).isEqualTo("test");
		assertThat(findedNotification.getContent().get(0).getUser().getId()).isEqualTo(userId);
		assertThat(findedNotification.getContent().get(0).getCreatedAt()).isEqualTo(createTime);
	}

	@Test
	void 기간이_지난_알림을_삭제한다() {
		//given
		LocalDateTime createTime = LocalDateTime.of(2025, 1, 1, 0, 0);
		LocalDateTime duration = LocalDateTime.now();
		User user = new User(1L);

		Notification notification = Notification.builder()
			.user(user)
			.content("test")
			.build();
		ReflectionTestUtils.setField(notification, "createdAt", createTime);

		Notification savedNotification = notificationRepository.save(notification);

		//when
		notificationRepository.deleteByCreatedAtBefore(duration);

		//then
		boolean exists = notificationRepository.existsById(savedNotification.getId());
		assertFalse(exists);
	}
}