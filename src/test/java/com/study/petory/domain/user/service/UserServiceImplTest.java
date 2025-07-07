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

		when(user.getDeletedAt()).thenReturn(null);
		when(user.getUserPrivateInfo()).thenReturn(info);
		when(user.getEmail()).thenReturn(email);
		when(user.getNickname()).thenReturn("닉네임");
		when(info.getName()).thenReturn("홍길동");
		when(info.getMobileNum()).thenReturn("010-1234-5678");

		when(userRepository.findByEmailWithUserRole(email)).thenReturn(Optional.of(user));

		// when
		UserProfileResponseDto result = userService.findMyProfile(email);

		// then
		assertThat(result.getEmail()).isEqualTo(email);
		assertThat(result.getNickname()).isEqualTo("닉네임");
		assertThat(result.getName()).isEqualTo("홍길동");
		assertThat(result.getMobileNum()).isEqualTo("010-1234-5678");
	}

	@Test
	void findMyProfile_탈퇴유저_예외발생() {

		// [given]
		String email = "deleted@example.com";

		User user = mock(User.class);
		when(user.getDeletedAt()).thenReturn(LocalDateTime.now());

		when(userRepository.findByEmailWithUserRole(email)).thenReturn(Optional.of(user));

		// then
		assertThatThrownBy(() -> userService.findMyProfile(email))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.USER_NOT_EXISTING.getMessage());
	}

	@Test
	void updateProfile_정상동작() {

		// [given]
		String email = "test@example.com";
		UserUpdateRequestDto dto = new UserUpdateRequestDto();
		dto.setNickname("new-nick");
		dto.setMobileNum("010-1111-2222");

		User user = mock(User.class);
		UserPrivateInfo info = mock(UserPrivateInfo.class);
		when(user.getDeletedAt()).thenReturn(null);
		when(user.getUserPrivateInfo()).thenReturn(info);

		when(userRepository.findByEmailWithUserRole(email)).thenReturn(Optional.of(user));

		// when
		userService.updateProfile(email, dto);

		// then
		verify(user).updateNickname("new-nick");
		verify(info).update("010-1111-2222");
	}
}
