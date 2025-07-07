package com.study.petory.domain.user.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.study.petory.common.exception.CustomException;
import com.study.petory.common.exception.enums.ErrorCode;
import com.study.petory.common.security.JwtProvider;
import com.study.petory.domain.user.dto.TokenResponseDto;
import com.study.petory.domain.user.dto.UserProfileResponseDto;
import com.study.petory.domain.user.dto.UserUpdateRequestDto;
import com.study.petory.domain.user.entity.Role;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.entity.UserPrivateInfo;
import com.study.petory.domain.user.entity.UserRole;
import com.study.petory.domain.user.entity.UserStatus;
import com.study.petory.domain.user.repository.UserRepository;

import io.jsonwebtoken.Claims;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private JwtProvider jwtProvider;

	@Mock
	private RedisTemplate<String, String> loginRefreshToken;

	@Mock
	private ValueOperations<String, String> valueOperations;

	@InjectMocks
	private UserServiceImpl userService;

	@Test
	void testLogin_ACTIVE_또는_DEACTIVATED_상태일때_로그인성공() {

		// [given]
		Long userId = 1L;

		// User 객체를 Mock 으로 생성
		User user = mock(User.class);
		when(user.getId()).thenReturn(userId);
		when(user.getEmail()).thenReturn("test@example.com");
		when(user.getNickname()).thenReturn("닉네임");
		// userStatus 를 ACTIVE 로 설정 (또는 DEACTIVATE 도 가능)
		when(user.getUserStatus()).thenReturn(UserStatus.ACTIVE);

		// User 권한을 가진 UserRole 객체 생성
		UserRole role = mock(UserRole.class);
		when(role.getRole()).thenReturn(Role.USER);
		when(user.getUserRole()).thenReturn(List.of(role));

		// JWT 토큰 생성 Mock 설정
		when(userRepository.findByIdWithUserRole(userId)).thenReturn(Optional.of(user));
		when(jwtProvider.createAccessToken(eq(userId), anyString(), anyString(), anyList())).thenReturn("access-token");
		when(jwtProvider.createRefreshToken(userId)).thenReturn("refresh-token");

		/* [when]
		 * 로그인 테스트 수행
		 */
		TokenResponseDto result = userService.testLogin(userId);

		/* [then]
		 * 생성된 토큰이 예상된 값과 일치하는지 검증
		 */
		assertThat(result.getAccessToken()).isEqualTo("access-token");
		assertThat(result.getRefreshToken()).isEqualTo("refresh-token");

		// RefreshToken 이 Redis 에 저장되었는지 검증
		verify(jwtProvider).storeRefreshToken(userId, "refresh-token");
	}

	@Test
	void testLogin_정지_또는_삭제_상태이면_예외발생() {

		// [given]
		Long userId = 2L;

		/*
		 * 로그인 불가 상태 유저 (Mock)
		 * userStatus = SUSPEND 또는 DELETED
		 */
		User user = mock(User.class);
		when(user.getUserStatus()).thenReturn(UserStatus.SUSPENDED);

		// 존재하는 유저를 레포지토리에서 반환하도록 설정
		when(userRepository.findByIdWithUserRole(userId)).thenReturn(Optional.of(user));

		/* [when & then]
		 * 로그인 시도 시 예외 발생 여부 검증
		 * 발생 여부가 CustomException 타입인지,
		 * 메시지 내용도 포함되어 있는지 검증
		 */
		assertThatThrownBy(() -> userService.testLogin(userId))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.LOGIN_UNAVAILABLE.getMessage());
	}

	@Test
	void findMyProfile_성공() {

		// [given]
		String email = "test@example.com";

		User user = mock(User.class);
		UserPrivateInfo info = mock(UserPrivateInfo.class);

		/*
		 * (정상 유저 필드 설정)
		 * 유저가 탈퇴하지 않았음을 설정
		 * 개인 정보 연결
		 * 유저 기본 정보 설정
		 * 유저 개인 정보 설정
		 */
		when(user.getDeletedAt()).thenReturn(null);
		when(user.getUserPrivateInfo()).thenReturn(info);
		when(user.getEmail()).thenReturn(email);
		when(user.getNickname()).thenReturn("닉네임");
		when(info.getName()).thenReturn("홍길동");
		when(info.getMobileNum()).thenReturn("010-1234-5678");

		// 레포지토리에서 해당 유저 조회했을 때 위의 mock 객체가 반환되도록 설정
		when(userRepository.findByEmailWithUserRole(email)).thenReturn(Optional.of(user));

		/* [when]
		 * 테스트 대상 메서드 호출 (프로필 정보 조회)
		 */
		UserProfileResponseDto result = userService.findMyProfile(email);

		/* [then]
		 * 반환값이 위에서 설정한 mock 객체의 값과 일치하는지 검증
		 */
		assertThat(result.getEmail()).isEqualTo(email);
		assertThat(result.getNickname()).isEqualTo("닉네임");
		assertThat(result.getName()).isEqualTo("홍길동");
		assertThat(result.getMobileNum()).isEqualTo("010-1234-5678");
	}

	@Test
	void findMyProfile_탈퇴유저_예외발생() {

		// [given]
		String email = "deleted@example.com";

		/*
		 * 탈퇴한 유저를 mock 으로 생성
		 * 탈퇴 시각을 현재 시각으로 설정해서 탈퇴 상태임을 나타냄
		 */
		User user = mock(User.class);
		when(user.getDeletedAt()).thenReturn(LocalDateTime.now());

		// 해당 유저를 이메일로 조회 시 위의 mock 객체가 반환되도록 설정
		when(userRepository.findByEmailWithUserRole(email)).thenReturn(Optional.of(user));

		/*
		 * [when & then]
		 * findMyProfile 호출 시, 탈퇴 유저이므로 예외가 발생해야 함
		 * 예외 타임 & 예외 메시지 일치하는지 확인
		 */
		assertThatThrownBy(() -> userService.findMyProfile(email))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.USER_NOT_EXISTING.getMessage());
	}

	@Test
	void updateProfile_정상동작() {

		/* [given]
		 * 사용자 이메일과 프로필 수정 Dto 생성
		 */
		String email = "test@example.com";
		UserUpdateRequestDto dto = new UserUpdateRequestDto();
		dto.setNickname("new-nick");
		dto.setMobileNum("010-1111-2222");

		// 유저 & 개인정보 mock 으로 생성
		User user = mock(User.class);
		UserPrivateInfo info = mock(UserPrivateInfo.class);

		/*
		 * 탈퇴되지 않은 유저로 설정
		 * 개인 정보 연결
		 */
		when(user.getDeletedAt()).thenReturn(null);
		when(user.getUserPrivateInfo()).thenReturn(info);

		// 이메일로 유저 조회 시 위의 mock 유저 반환되도록 설정
		when(userRepository.findByEmailWithUserRole(email)).thenReturn(Optional.of(user));

		/* [when]
		 * 유저 정보 수정 메서드 호출
		 */
		userService.updateProfile(email, dto);

		/* [then]
		 * 닉네임 및 전화번호 값 일치하는지 검증
		 */
		verify(user).updateNickname("new-nick");
		verify(info).update("010-1111-2222");
	}

	@Test
	void logout_정상동작() {

		// [given]
		String token = "Bearer abc.def.ghi";
		String pureToken = "abc.def.ghi";
		Long userId = 123L;

		// JWT 의 Claims 를 mock 으로 생성하여 토큰 정보 세팅
		Claims claims = mock(Claims.class);
		when(claims.getSubject()).thenReturn(userId.toString());
		when(claims.getExpiration()).thenReturn(new Date(System.currentTimeMillis() + 10_000));

		// jwtProvider 관련 메서드 모킹 (토큰 파싱 & claims)
		when(jwtProvider.subStringToken(token)).thenReturn(pureToken);
		when(jwtProvider.getClaims(pureToken)).thenReturn(claims);
		when(jwtProvider.getClaims(token)).thenReturn(claims);

		// RedisTemplate 설정 (ValueOperations 반환 설정)
		when(loginRefreshToken.opsForValue()).thenReturn(valueOperations);

		/* [when]
		 * 로그아웃 메서드 수행
		 */
		userService.logout(token);

		/* [then]
		 * (AccessToken 을 블랙리스트에 저장했는지 검증)
		 * key: "BLACKLIST_{token}"
		 * value: "logout"
		 * TTL: 만료 시간까지 남은 밀리초
		 */
		verify(valueOperations).set(
			eq("BLACKLIST_" + pureToken),
			eq("logout"),
			anyLong(),
			eq(TimeUnit.MILLISECONDS)
		);

		// 해당 유저의 RefreshToken 삭제 메서드 수행됐는지 검증
		verify(jwtProvider).deleteRefreshToken(userId);
	}

	@Test
	void deleteAccount_정상_softDelete() {

		/* [given]
		 * 삭제 요청을 받을 유저 이메일
		 * 탈퇴 대상 유저를 mock 으로 생성 (userStatus: ACTIVE, deletedAt: null)
		 */
		String email = "delete@test.com";
		User user = mock(User.class);
		when(user.getUserStatus()).thenReturn(UserStatus.ACTIVE);
		when(user.getDeletedAt()).thenReturn(null);

		// 이메일 기준 유저 조회 시 위의 mock 반환되도록 설정
		when(userRepository.findByEmailWithUserRole(email)).thenReturn(Optional.of(user));

		/* [when]
		 * soft delete 메서드 수행
		 */
		userService.deleteAccount(email);

		/* [then]
		 * 탈퇴 처리 메서드 호출됐는지 검증
		 * userStatus DELETED 로 변경됐는지 검증
		 */
		verify(user).deactivateEntity();
		verify(user).updateStatus(UserStatus.DELETED);
	}

	@Test
	void deleteAccount_이미삭제된유저_예외() {

		/* [given]
		 * 유저 객체 mock 으로 생성 (userStatus: DELETED)
		 */
		String email = "already@deleted.com";
		User user = mock(User.class);
		when(user.getUserStatus()).thenReturn(UserStatus.DELETED);

		// 이메일 기준 유저 조회 시 위의 mock 유저 반환되도록 설정
		when(userRepository.findByEmailWithUserRole(email)).thenReturn(Optional.of(user));

		/* [when & then]
		 * 이미 탈퇴한 유저가 다시 탈퇴 시도할 경우 예외 타입 & 예외 메시지 일치하는지 검증
		 */
		assertThatThrownBy(() -> userService.deleteAccount(email))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.USER_ALREADY_DELETED.getMessage());
	}

	@Test
	void findUserById_탈퇴유저일경우_예외발생() {

		// [given]
		Long userId = 1L;
		User user = mock(User.class);
		when(user.getDeletedAt()).thenReturn(LocalDateTime.now());

		// [when]
		when(userRepository.findByIdWithUserRole(userId)).thenReturn(Optional.of(user));

		// [then]
		assertThatThrownBy(() -> userService.findUserById(userId))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.USER_NOT_EXISTING.getMessage());
	}

	@Test
	void findUserByIdWithUserStatus_존재하지않는경우_예외발생() {

		// [given]
		Long userId = 1L;

		// [when]
		when(userRepository.findByIdWithUserRoleAndUserStatus(userId, UserStatus.ACTIVE))
			.thenReturn(Optional.empty());

		// [then]
		assertThatThrownBy(() -> userService.findUserByIdWithUserStatus(userId, UserStatus.ACTIVE))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.USER_NOT_FOUND.getMessage());
	}

	@Test
	void findUserByEmail_정상조회() {

		// [given]
		String email = "test@email.com";
		User user = mock(User.class);
		when(user.getDeletedAt()).thenReturn(null);

		when(userRepository.findByEmailWithUserRole(email)).thenReturn(Optional.of(user));

		// [when]
		User result = userService.findUserByEmail(email);

		// [then]
		assertThat(result).isEqualTo(user);
	}

	@Test
	void findUserByEmail_삭제된유저_예외() {

		// [given]
		String email = "deleted@email.com";
		User user = mock(User.class);
		when(user.getDeletedAt()).thenReturn(LocalDateTime.now());

		// [when]
		when(userRepository.findByEmailWithUserRole(email)).thenReturn(Optional.of(user));

		// [then]
		assertThatThrownBy(() -> userService.findUserByEmail(email))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.USER_NOT_EXISTING.getMessage());
	}
}
