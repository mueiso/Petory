package com.study.petory.domain.user.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.petory.common.config.QueryDSLConfig;
import com.study.petory.domain.user.entity.Role;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.entity.UserRole;

import jakarta.persistence.EntityManager;

@DataJpaTest
@Import(QueryDSLConfig.class)
@EntityScan(basePackages = "com.study.petory.domain")
class UserQueryRepositoryTest {

	@Autowired
	private EntityManager em;

	private UserQueryRepository userQueryRepository;

	private User testUser;

	@BeforeEach
	void setUp() {

		userQueryRepository = new UserQueryRepositoryImpl(new JPAQueryFactory(em));

		/* [given]
		 * 유저 및 유저 권한 저장
		 */
		UserRole userRole = UserRole.builder()
			.role(Role.USER)
			.build();
		em.persist(userRole);

		testUser = User.builder()
			.email("test@example.com")
			.nickname("tester")
			.userRole(List.of(userRole))
			.build();
		em.persist(testUser);

		em.flush();
		em.clear();
	}

	@Test
	void findByEmailWithUserRole_유저_조회_성공() {

		// when
		Optional<User> result = userQueryRepository.findByEmailWithUserRole("test@example.com");

		// then
		assertThat(result).isPresent();
		assertThat(result.get().getEmail()).isEqualTo("test@example.com");
		assertThat(result.get().getUserRole()).isNotNull();
		assertThat(result.get().getUserRole().get(0).getRole()).isEqualTo(Role.USER);
	}
}
