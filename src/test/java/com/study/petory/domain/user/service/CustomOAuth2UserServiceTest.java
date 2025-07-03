package com.study.petory.domain.user.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.study.petory.domain.user.entity.Role;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.entity.UserPrivateInfo;
import com.study.petory.domain.user.entity.UserRole;
import com.study.petory.domain.user.entity.UserStatus;
import com.study.petory.domain.user.repository.UserRepository;

class CustomOAuth2UserServiceTest {

	// 공통 테스트용 사용자 상수
	private static final String TEST_EMAIL = "test@email.com";
	private static final String TEST_NAME = "테스트용 이름";
	private static final String NEW_EMAIL = "new@email.com";
	private static final String NEW_NAME = "신규유저 이름";

	@Mock
	private UserRepository userRepository;

	@Mock
	private OAuth2UserRequest userRequest;

	@InjectMocks
	private CustomOAuth2UserService customOAuth2UserService;

	// @Mock, @InjectMocks 초기화
	public CustomOAuth2UserServiceTest() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void loadUser_기존_유저_정상_조회() {

		/* [given]
		 * 기존 User 객체 생성
		 */
		User user = createUserWithStatus(UserStatus.ACTIVE, TEST_EMAIL, TEST_NAME);

		/*
		 * OAuth2 인증 서버에서 받아온 사용자 정보 시뮬레이션
		 * 권한 목록
		 * 사용자 정보
		 * usernameKey
		 */
		OAuth2User oAuth2User = new DefaultOAuth2User(
			List.of(() -> "ROLE_USER"),
			Map.of("email", TEST_EMAIL, "name", TEST_NAME),
			"email"
		);

		// ClientRegistration Mock 설정 (예: Google)
		ClientRegistration clientRegistration = mock(ClientRegistration.class);
		given(clientRegistration.getRegistrationId()).willReturn("google");
		given(userRequest.getClientRegistration()).willReturn(clientRegistration);
		// Repository 에서 기존 User 반환
		given(userRepository.findByEmailWithUserRole(TEST_EMAIL)).willReturn(Optional.of(user));

		/*
		 * 실제 서비스는 spy 로 감싸서 내부 loadUser Override
		 * spy: Mockito 에서 제공하는 기능 중 하나로, 원본 객체의 일부 동작만 가짜(mock)로 대체하고 나머지는 실제 동작을 수행하게 만들고 싶을 때 사용
		 * mock = 완전히 가짜 / spy = 실제 객체
		 */
		CustomOAuth2UserService spyService = spy(customOAuth2UserService);
		doReturn(oAuth2User).when((DefaultOAuth2UserService)spyService).loadUser(userRequest);

		/* [when]
		 * 테스트 대상 메서드 호출
		 */
		OAuth2User result = spyService.loadUser(userRequest);

		/* [then]
		 * 이름(email) 확인
		 * 권한 포함 여부 확인
		 */
		assertThat(result.getName()).isEqualTo(TEST_EMAIL);
		assertThat(result.getAuthorities()).anyMatch(a -> a.getAuthority().equals("ROLE_USER"));
	}

	// 테스트용 유저 객체 생성 유틸 메서드
	private User createUserWithStatus(UserStatus status, String email, String name) {

		UserPrivateInfo privateInfo = UserPrivateInfo.builder()
			.authId("google")
			.name(name)
			.mobileNum("01012345678")
			.build();

		User user = User.builder()
			.nickname(name)
			.email(email)
			.userPrivateInfo(privateInfo)
			.userRole(new ArrayList<>(List.of(UserRole.builder().role(Role.USER).build())))
			.build();

		user.updateStatus(status);
		return user;
	}
}
