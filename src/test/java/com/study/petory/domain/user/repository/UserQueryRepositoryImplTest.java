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

		// QueryDSL 을 사용하기 위해 JPAQueryFactory 를 직접 생성해 레포지토리 구현체에 주입
		userQueryRepository = new UserQueryRepositoryImpl(new JPAQueryFactory(em));

		/* [given]
		 * 테스트 유저 권한 생성 및 저장
		 * 권한 설정
		 * .persist(): 권한을 DB 에 저장
		 */
		UserRole userRole = UserRole.builder()
			.role(Role.USER)
			.build();
		em.persist(userRole);

		// 테스트용 유저 생성, 권한은 리스트 형태로 포함
		testUser = User.builder()
			.email("test@example.com")
			.nickname("tester")
			.userRole(List.of(userRole))
			.build();
		em.persist(testUser);

		/*
		 * .flush(): 영속성 컨텍스트에 저장된 내용을 DB에 반영
		 * .clear(): 1차 캐시 초기화하여 이후의 조회는 쿼리를 통해 수행되도록 함
		 */
		em.flush();
		em.clear();
	}

	@Test
	void findByEmailWithUserRole_유저_조회_성공() {

		/* [when]
		 * 이메일을 기준으로 유저를 조회 (권한 포함)
		 */
		Optional<User> result = userQueryRepository.findByEmailWithUserRole("test@example.com");

		/* [then]
		 * 결과가 존재하는지 확인
		 * 이메일 확인
		 * 권한 정보 있는지 확인
		 * 첫 번째 권한이 USER 인지 확인
		 */
		assertThat(result).isPresent();
		assertThat(result.get().getEmail()).isEqualTo("test@example.com");
		assertThat(result.get().getUserRole()).isNotNull();
		assertThat(result.get().getUserRole().get(0).getRole()).isEqualTo(Role.USER);
	}

	@Test
	void findByIdWithUserRole_유저_조회_성공() {

		// when
		Optional<User> result = userQueryRepository.findByIdWithUserRole(testUser.getId());

		// then
		assertThat(result).isPresent();
		assertThat(result.get().getId()).isEqualTo(testUser.getId());
		assertThat(result.get().getUserRole()).isNotNull();
		assertThat(result.get().getUserRole().get(0).getRole()).isEqualTo(Role.USER);
	}
}
