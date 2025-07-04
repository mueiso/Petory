package com.study.petory.domain.user.repository;

import static org.assertj.core.api.Assertions.*;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.study.petory.common.config.QueryDSLConfig;
import com.study.petory.domain.user.entity.Role;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.entity.UserPrivateInfo;
import com.study.petory.domain.user.entity.UserRole;
import com.study.petory.domain.user.entity.UserStatus;

@DataJpaTest
@Import(QueryDSLConfig.class)
@EntityScan(basePackages = "com.study.petory.domain")
class UserRepositoryTest {

	@Autowired
	private UserRepository userRepository;

	@Test
	// 삭제 예정자 조회: DEACTIVATED 상태, deletedAt 85~90일 전
	void findUsers_휴면_상테_삭제_예정자_조회_성공() {

		LocalDateTime now = LocalDateTime.now().withNano(0);
		LocalDateTime from = now.minusDays(90);
		LocalDateTime to = now.minusDays(85);

		createUser(UserStatus.DEACTIVATED, now.minusDays(87), now.minusDays(100)); // 포함됨
		createUser(UserStatus.DEACTIVATED, now.minusDays(80), now.minusDays(100)); // 제외됨
		createUser(UserStatus.ACTIVE, now.minusDays(87), now.minusDays(100));      // 제외됨

		List<User> result = userRepository.findByUserStatusAndDeletedAtBetween(UserStatus.DEACTIVATED, from, to);

		assertThat(result).hasSize(1);
	}

	private User createUser(UserStatus status, LocalDateTime deletedAt, LocalDateTime updatedAt) {

		UserPrivateInfo privateInfo = UserPrivateInfo.builder()
			.authId("auth_" + status.name())
			.name("테스트 유저")
			.mobileNum("01012345678")
			.build();

		User user = User.builder()
			.nickname("닉_" + status.name())
			.email("test_" + status.name() + "@email.com")
			.userPrivateInfo(privateInfo)
			.userRole(List.of(UserRole.builder().role(Role.USER).build()))
			.build();

		user.updateStatus(status);

		// 리플렉션으로 deletedAt, updatedAt 설정
		if (deletedAt != null) {
			setField(user, "deletedAt", deletedAt);
		}
		if (updatedAt != null) {
			setField(user, "updatedAt", updatedAt);
		}

		return userRepository.save(user);
	}

	// 필드값 설정 위한 유틸 메서드
	private void setField(Object target, String fieldName, Object value) {

		try {
			Field field = target.getClass().getSuperclass().getDeclaredField(fieldName);
			field.setAccessible(true);
			field.set(target, value);
		} catch (Exception e) {
			throw new RuntimeException("리플렉션 필드 설정 실패: " + fieldName, e);
		}
	}
}
