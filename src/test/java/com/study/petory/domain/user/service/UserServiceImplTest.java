package com.study.petory.domain.user.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import com.study.petory.common.exception.CustomException;
import com.study.petory.common.exception.enums.ErrorCode;
import com.study.petory.common.security.JwtProvider;
import com.study.petory.domain.user.dto.TokenResponseDto;
import com.study.petory.domain.user.entity.Role;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.entity.UserRole;
import com.study.petory.domain.user.entity.UserStatus;
import com.study.petory.domain.user.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

	@Mock private UserRepository userRepository;
	@Mock private JwtProvider jwtProvider;
	@Mock private RedisTemplate<String, String> loginRefreshToken;

	@InjectMocks
	private UserServiceImpl userService;

	@Test
	void testLogin_ACTIVE_또는_DEACTIVATED_상태일때_로그인성공() {

		// given
		Long userId = 1L;
		User user = mock(User.class);

		when(user.getId()).thenReturn(userId);
		when(user.getEmail()).thenReturn("test@example.com");
		when(user.getNickname()).thenReturn("홍길동");
		when(user.getUserStatus()).thenReturn(UserStatus.ACTIVE); // 또는 DEACTIVATED

		UserRole role = mock(UserRole.class);
		when(role.getRole()).thenReturn(Role.USER);
		when(user.getUserRole()).thenReturn(List.of(role));

		when(userRepository.findByIdWithUserRole(userId)).thenReturn(Optional.of(user));
		when(jwtProvider.createAccessToken(eq(userId), anyString(), anyString(), anyList())).thenReturn("access-token");
		when(jwtProvider.createRefreshToken(userId)).thenReturn("refresh-token");

		// when
		TokenResponseDto result = userService.testLogin(userId);

		// then
		assertThat(result.getAccessToken()).isEqualTo("access-token");
		assertThat(result.getRefreshToken()).isEqualTo("refresh-token");
		verify(jwtProvider).storeRefreshToken(userId, "refresh-token");
	}

	@Test
	void testLogin_정지_또는_삭제_상태이면_예외발생() {

		// given
		Long userId = 2L;
		User user = mock(User.class);
		when(user.getUserStatus()).thenReturn(UserStatus.SUSPENDED); // 또는 UserStatus.DELETED
		when(userRepository.findByIdWithUserRole(userId)).thenReturn(Optional.of(user));

		// when & then
		assertThatThrownBy(() -> userService.testLogin(userId))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.LOGIN_UNAVAILABLE.getMessage());
	}
}
