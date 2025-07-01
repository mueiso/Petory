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

import com.study.petory.common.exception.CustomException;
import com.study.petory.common.exception.enums.ErrorCode;
import com.study.petory.common.security.JwtProvider;
import com.study.petory.domain.user.dto.TokenResponseDto;
import com.study.petory.domain.user.entity.Role;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.entity.UserPrivateInfo;
import com.study.petory.domain.user.entity.UserRole;
import com.study.petory.domain.user.entity.UserStatus;

import io.jsonwebtoken.Jwts;

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
		assertThat(result.getAccessToken()).isEqualTo("access-token");
		assertThat(result.getRefreshToken()).isEqualTo("refresh-token");
		assertThat(user.getUserStatus()).isEqualTo(UserStatus.ACTIVE);
	}

	@Test
	void issueToken_SUSPENDED_상태_로그인_불가_예외발생() {

		/* [given]
		 * userStatus SUSPENDED 상태의 유저 객체 생성
		 * 한번 더 명시적으로 상태 설정
		 * ID 가 null 인 경우 방지 위해 리플렉션 통해 ID 강제 주입
		 */
		User user = createUserWithStatus(UserStatus.SUSPENDED);
		user.updateStatus(UserStatus.SUSPENDED);
		ReflectionTestUtils.setField(user, "id", 3L);

		// mock 설정 - 이메일로 유저 조회 시 위에서 만든 suspended 유저를 반환
		given(userService.findUserByEmail(anyString())).willReturn(user);

		//  - 로그인 시도 시 예외 발생 검증
		/* [when & then]
		 * AuthService 의 issueToken 호출 시 (로그인 시도 시) 예외 발생 여부 확인
		 * 예외 타입 확인
		 * 에러 메시지 일치하는지 검증
		 */
		assertThatThrownBy(() -> authService.issueToken(user))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining("로그인 불가합니다. 계정이 정지되었거나, 탈퇴한 유저입니다.");
	}

	@Test
	void issueToken_아이디_null이면_예외발생() {

		/* [given]
		 * ID가 null 인 유저 객체를 ACTIVE 상태로 생성 (ID는 설정하지 않아서 기본적으로 null 상태)
		 */
		User user = createUserWithStatus(UserStatus.ACTIVE);

		// mock 설정 - 이메일로 유저 조회 시 위 유저 반환
		given(userService.findUserByEmail(anyString())).willReturn(user);

		/* [when]
		 * 토큰 발급 시도
		 * ID 가 없으므로 예외 발생
		 */
		CustomException exception = catchThrowableOfType(
			() -> authService.issueToken(user),
			CustomException.class
		);

		/* [then]
		 * USER_ID_NOT_GENERATED 예외 코드 검증
		 */
		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_ID_NOT_GENERATED);
	}

	@Test
	void issueToken_ACTIVE_상태_유저_토큰_정상_발급() {

		/* [given]
		 * userStatus ACTIVE 상태의 유저 생성
		 * 유저 ID 설정
		 */
		User user = createUserWithStatus(UserStatus.ACTIVE);
		user.updateStatus(UserStatus.ACTIVE);
		ReflectionTestUtils.setField(user, "id", 10L);

		/*
		 * mock 설정 - 유저 조회 시 위 유저 반환
		 * mock 설정 - accessToken 생성 요청 시 "accessToken" 리턴
		 * mock 설정 - refreshToken 생성 요청 시 "refreshToken" 리턴
		 */
		given(userService.findUserByEmail(anyString())).willReturn(user);
		given(jwtProvider.createAccessToken(any(), any(), any(), any())).willReturn("access-token");
		given(jwtProvider.createRefreshToken(any())).willReturn("refresh-token");

		/* [when]
		 * 토큰 발급 실행
		 */
		TokenResponseDto result = authService.issueToken(user);

		/* [then]
		 * 반환값이 null 이 아닌지 존재 여부 확인
		 * accessToken 값이 예상대로인지 확인
		 * refreshToken 값이 예상대로인지 확인
		 * userStatus 여전히 ACTIVE 인지 검증
		 */
		assertThat(result).isNotNull();
		assertThat(result.getAccessToken()).isEqualTo("access-token");
		assertThat(result.getRefreshToken()).isEqualTo("refresh-token");
		assertThat(user.getUserStatus()).isEqualTo(UserStatus.ACTIVE);
	}

	@Test
	void reissue_refreshToken_정상_재발급_성공() {

		/* [given]
		 * 테스트용 user 객체 생성
		 * ID 세팅
		 */
		User user = createUserWithStatus(UserStatus.ACTIVE);
		ReflectionTestUtils.setField(user, "id", 100L);

		/*
		 * 만료된 AccessToken
		 * Bearer 포함된 refreshToken
		 * Bearer 제거된 토큰
		 */
		String expiredAccessToken = "expired-access-token";
		String refreshTokenWithBearer = "Bearer valid-refresh-token";
		String pureRefreshToken = "valid-refresh-token";
		Long userId = 100L;

		/*
		 * mock 설정 - AccessToken 만료 상태
		 * mock 설정 - Bearer 제거
		 * mock 설정 - Claims 에서 userId 추출
		 * mock 설정 - refreshToken 유효성 확인
		 * mock 설정 - 유저 조회
		 * mock 설정 - 새 AccessToken 생성
		 * mock 설정 - 새 RefreshToken 생성
		 */
		given(jwtProvider.isAccessTokenExpired(expiredAccessToken)).willReturn(true);
		given(jwtProvider.subStringToken(refreshTokenWithBearer)).willReturn(pureRefreshToken);
		given(jwtProvider.getClaims(pureRefreshToken)).willReturn(
			Jwts.claims().setSubject(String.valueOf(userId)));
		given(jwtProvider.isValidRefreshToken(userId, pureRefreshToken)).willReturn(true);
		given(userService.findUserById(userId)).willReturn(user);
		given(jwtProvider.createAccessToken(any(), any(), any(), any())).willReturn(
			"new-access-token");
		given(jwtProvider.createRefreshToken(userId)).willReturn("new-refresh-token");

		/* [when]
		 * 재발급 요청
		 */
		TokenResponseDto result = authService.reissue(expiredAccessToken, refreshTokenWithBearer);

		/* [then]
		 * 토큰 재발급 결과 검증
		 */
		assertThat(result).isNotNull();
		assertThat(result.getAccessToken()).isEqualTo("new-access-token");
		assertThat(result.getRefreshToken()).isEqualTo("new-refresh-token");
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
