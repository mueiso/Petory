package com.study.petory.domain.user.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.study.petory.common.security.JwtProvider;
import com.study.petory.domain.user.dto.TokenResponseDto;
import com.study.petory.domain.user.entity.Role;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.entity.UserPrivateInfo;
import com.study.petory.domain.user.entity.UserRole;
import com.study.petory.domain.user.entity.UserStatus;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

	@Mock
	private UserService userService;

	@Mock
	private JwtProvider jwtProvider;

	@InjectMocks
	private AuthServiceImpl authService;

	@Test
	void issueToken_DEACTIVATED_90일_이내_로그인_성공_복구() {

		// given
		User user = createUserWithStatus(UserStatus.DEACTIVATED);
		user.updateStatus(UserStatus.DEACTIVATED);
		user.deactivateEntity(); // deletedAt 등록됨
		ReflectionTestUtils.setField(user, "id", 1L);

		// deletedAt 을 현재 기준 89일 전으로 설정
		ReflectionTestUtils.setField(user, "deletedAt", LocalDateTime.now().minusDays(89));

		given(userService.findUserByEmail(anyString())).willReturn(user);
		given(jwtProvider.createAccessToken(any(), any(), any(), any())).willReturn("access-token");
		given(jwtProvider.createRefreshToken(any())).willReturn("refresh-token");

		// when
		TokenResponseDto result = authService.issueToken(user);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getAccessToken()).isEqualTo("access-token");
		assertThat(result.getRefreshToken()).isEqualTo("refresh-token");
		assertThat(user.getUserStatus()).isEqualTo(UserStatus.ACTIVE); // 상태 복구되었는지 확인
	}

	// 테스트용 유저 객체 생성 유틸 메서드
	private User createUserWithStatus(UserStatus status) {

		UserPrivateInfo privateInfo = UserPrivateInfo.builder()
			.authId("auth123")
			.name("나이름")
			.mobileNum("01012345678")
			.build();

		User user = User.builder()
			.nickname("닉네임")
			.email("test@email.com")
			.userPrivateInfo(privateInfo)
			.userRole(List.of(UserRole.builder().role(Role.USER).build()))
			.build();

		user.updateStatus(status);
		return user;
	}
}
