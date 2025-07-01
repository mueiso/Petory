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

		/* [given]
		 * userStatus 가 DEACTIVATED 인 테스트 유저 생성
		 * 명시적으로 DEACTIVATED 설정
		 * deletedAt 값을 현재 시각으로 설정
		 * 테스트를 위해 user 의 id 필드 강제 설정
		 */
		User user = createUserWithStatus(UserStatus.DEACTIVATED);
		user.updateStatus(UserStatus.DEACTIVATED);
		user.deactivateEntity();
		ReflectionTestUtils.setField(user, "id", 1L);

		// deletedAt을 현재 시각 기준 89일 전으로 설정 → 복구 조건 만족 (90일 이내 로그인)
		ReflectionTestUtils.setField(user, "deletedAt", LocalDateTime.now().minusDays(89));

		/*
		 * userService.findUserByEmail() 호출 시 위에서 만든 user 리턴하도록 설정
		 * jwtProvider.createAccessToken() 호출 시 accessToken 리턴
		 * jwtProvider.createRefreshToken() 호출 시 refreshToken 리턴
		 */
		given(userService.findUserByEmail(anyString())).willReturn(user);
		given(jwtProvider.createAccessToken(any(), any(), any(), any())).willReturn("access-token");
		given(jwtProvider.createRefreshToken(any())).willReturn("refresh-token");

		/* [when]
		 * 실제 테스트 대상 메서드 호출
		 */
		TokenResponseDto result = authService.issueToken(user);

		/* [then]
		 * 반환된 TokenResponseDto 가 null 이 아님을 검증
		 * accessToken 값 검증
		 * refreshToken 값 검증
		 * userStatus 가 ACTIVE 로 복구되었는지 검증
		 */
		assertThat(result).isNotNull();
		assertThat(result.getAccessToken()).isEqualTo("access-token");
		assertThat(result.getRefreshToken()).isEqualTo("refresh-token");
		assertThat(user.getUserStatus()).isEqualTo(UserStatus.ACTIVE);
	}

	@Test
	void issueToken_DELETED_복구_성공() {

		/* [given]
		 * 테스트용 유저를 userStatus DELETED 상태로 생성
		 * 한번 더 명시적으로 상태 설정
		 * deletedAt 필드에 현재 시각 저장 (탈퇴 상태 구현)
		 * ID 가 null 인 경우 방지 위해 리플렉션 통해 ID 강제 주입
		 */
		User user = createUserWithStatus(UserStatus.DELETED);
		user.updateStatus(UserStatus.DELETED);
		user.deactivateEntity();
		ReflectionTestUtils.setField(user, "id", 2L);

		/*
		 * mock 설정 - 이메일로 유저 조회 시 위에서 만든 user 리턴
		 * mock 설정 - accessToken 생성 요청 시 "accessToken" 리턴
		 * mock 설정 - refreshToken 생성 요청 시 "refreshToken" 리턴
		 */
		given(userService.findUserByEmail(anyString())).willReturn(user);
		given(jwtProvider.createAccessToken(any(), any(), any(), any())).willReturn("access-token");
		given(jwtProvider.createRefreshToken(any())).willReturn("refresh-token");

		/* [when]
		 * 태스트 대상 메서드 호출 (토큰 발급 메서드 실행)
		 */
		TokenResponseDto result = authService.issueToken(user);

		/* [then]
		 * 반환값이 null 이 아닌지 존재 여부 확인
		 * accessToken 값이 예상대로인지 확인
		 * refreshToken 값이 예상대로인지 확인
		 * userStatus ACTIVE 로 복구되었는지 검증
		 */
		assertThat(result).isNotNull();
		assertThat(result.getAccessToken()).isEqualTo("access-token"); // accessToken 값 확인
		assertThat(result.getRefreshToken()).isEqualTo("refresh-token"); // refreshToken 값 확인
		assertThat(user.getUserStatus()).isEqualTo(UserStatus.ACTIVE); // 상태가 ACTIVE로 복구되었는지 확인
	}

	// 테스트용 유저 객체를 생성하는 유틸 메서드
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

		// 전달받은 상태로 userStatus 설정
		user.updateStatus(status);

		return user;
	}
}
