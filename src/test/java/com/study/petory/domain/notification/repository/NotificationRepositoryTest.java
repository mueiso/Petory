// package com.study.petory.domain.notification.repository;
//
// import static org.junit.jupiter.api.Assertions.*;
//
// import java.time.LocalDateTime;
//
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
// import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
// import org.springframework.context.annotation.Import;
// import org.springframework.test.util.ReflectionTestUtils;
//
// import com.study.petory.common.config.QueryDSLConfig;
// import com.study.petory.domain.notification.entity.Notification;
// import com.study.petory.domain.user.entity.User;
//
// import jakarta.persistence.EntityManager;
//
// @DataJpaTest
// @Import(QueryDSLConfig.class)
// class NotificationRepositoryTest {
//
// 	@Autowired
// 	private NotificationRepository notificationRepository;
//
// 	@Autowired
// 	private EntityManager em;
//
// 	@Test
// 	void 기간이_지난_알림을_삭제한다() {
// 		//given
// 		LocalDateTime createTime = LocalDateTime.of(2025, 1, 1, 0, 0);
// 		LocalDateTime duration = LocalDateTime.now();
// 		User user = new User(1L);
//
// 		Notification notification = Notification.builder()
// 			.user(user)
// 			.content("test")
// 			.build();
// 		ReflectionTestUtils.setField(notification, "id", 1L);
// 		ReflectionTestUtils.setField(notification, "createdAt", createTime);
//
// 		notificationRepository.save(notification);
//
// 		//when
// 		notificationRepository.deleteByCreatedAtBefore(duration);
// 		em.flush();
// 		em.clear();
//
// 		//then
// 		boolean exists = notificationRepository.existsById(1L);
// 		assertFalse(exists);
// 	}
// }