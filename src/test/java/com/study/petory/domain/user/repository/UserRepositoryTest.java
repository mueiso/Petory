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
	// userStatus DEACTIVATED 상테이면서 deletedAt 값이 85~90일 전 사이인 사용자 조회
	void findUsers_휴면_상테_삭제_예정자_조회_성공() {

		/*
		 * 비교 정확도 향상 위해 nano 단위는 제거
		 * 조회 시작 기준일
		 * 조회 종료 기준일
		 */
		LocalDateTime now = LocalDateTime.now().withNano(0);
		LocalDateTime from = now.minusDays(90);
		LocalDateTime to = now.minusDays(85);

		/*
		 * 기준 범위에 포함되는 유저: deletedAt 이 87일전
		 * 기준 범위에 제외되는 유저들: deletedAt 이 80일 전이라 범위 밖
		 * 기준 범위에 제외되는 유저들: userStatus 가 ACTIVE
		 */
		createUser(UserStatus.DEACTIVATED, now.minusDays(87), now.minusDays(100)); // 포함됨
		createUser(UserStatus.DEACTIVATED, now.minusDays(80), now.minusDays(100)); // 제외됨
		createUser(UserStatus.ACTIVE, now.minusDays(87), now.minusDays(100));      // 제외됨

		// 테스트 대상 쿼리 실행
		List<User> result = userRepository.findByUserStatusAndDeletedAtBetween(UserStatus.DEACTIVATED, from, to);

		// 1명만 조회되는지 확인
		assertThat(result).hasSize(1);
	}

	@Test
	// userStatus DEACTIVATED 또는 DELETED 상태이면서 deletedAt 값이 90일 초과된 사용자 조회
	void findUsers_휴면_또는_탈퇴_상태_삭제_예정자_조회_성공() {

		// 기준 시점: 90일 전
		LocalDateTime base = LocalDateTime.now().withNano(0).minusDays(90);

		/*
		 * 기준 범위에 포함되는 유저: deletedAt 이 92일전
		 * 기준 범위에 포함되는 유저: deletedAt 이 91일전
		 * 기준 범위에 제외되는 유저: userStatus 가 ACTIVE
		 */
		createUser(UserStatus.DEACTIVATED, base.minusDays(2), base.minusDays(10));
		createUser(UserStatus.DELETED, base.minusDays(1), base.minusDays(5));
		createUser(UserStatus.ACTIVE, base.minusDays(1), base.minusDays(5));

		/*
		 * 삭제 대상 리스트 구성
		 * 테스트 대상 쿼리 실행
		 */
		List<UserStatus> statusList = List.of(UserStatus.DEACTIVATED, UserStatus.DELETED);
		List<User> result = userRepository.findByUserStatusInAndDeletedAtBefore(statusList, base);

		// 2명 조회되는지 확인
		assertThat(result).hasSize(2);
	}

	@Test
	@DisplayName("휴면 후 경과 사용자 조회: DEACTIVATED 상태, deletedAt < 기준일")
	void findDormantUsersExceededPeriod() {

		LocalDateTime base = LocalDateTime.now().withNano(0).minusDays(30);

		createUser(UserStatus.DEACTIVATED, base.minusDays(10), base.minusDays(40)); // 포함
		createUser(UserStatus.DEACTIVATED, base.plusDays(5), base.minusDays(40));   // 제외
		createUser(UserStatus.ACTIVE, base.minusDays(10), base.minusDays(40));      // 제외

		List<User> result = userRepository.findByUserStatusAndDeletedAtBefore(UserStatus.DEACTIVATED, base);

		assertThat(result).hasSize(1);
	}

	@Test
	@DisplayName("비활동 사용자 조회: ACTIVE 상태, updatedAt < 기준일")
	void findInactiveUsers() {

		LocalDateTime standard = LocalDateTime.now().withNano(0).minusDays(180);

		createUser(UserStatus.ACTIVE, null, standard.minusDays(10)); // 포함
		createUser(UserStatus.ACTIVE, null, standard.plusDays(5));   // 제외
		createUser(UserStatus.DELETED, null, standard.minusDays(10));// 제외

		List<User> result = userRepository.findByUserStatusAndUpdatedAtBefore(UserStatus.ACTIVE, standard);

		assertThat(result).hasSize(1);
	}

	// 테스트에 사용할 User 생성 위한 유틸 메서드
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

	// 리플렉션으로 부모 클래스 필드 값 설정 위한 유틸 메서드 (TimeFeatureBasedEntity 의 private 필드 대응용)
	private void setField(Object target, String fieldName, Object value) {

		try {
			// 부모 클래스에서 필드 검색 (deletedAt, updatedAt)
			Field field = target.getClass().getSuperclass().getDeclaredField(fieldName);
			// 접근 가능하도록 설정
			field.setAccessible(true);
			// 값 설정
			field.set(target, value);
		} catch (Exception e) {
			throw new RuntimeException("리플렉션 필드 설정 실패: " + fieldName, e);
		}
	}
}
