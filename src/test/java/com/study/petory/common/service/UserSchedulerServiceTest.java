package com.study.petory.common.service;

import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.entity.UserPrivateInfo;
import com.study.petory.domain.user.entity.UserStatus;
import com.study.petory.domain.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserSchedulerServiceTest {

	@Mock
	private EmailService emailService;  // 이메일 발송 기능을 모킹 (실제 메일 전송 X)

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private UserSchedulerService userSchedulerService;

	@Test
	void testSendDeactivationWarningEmails_85일_지나면_이메일_전송() {

		LocalDateTime now = LocalDateTime.of(2025, 7, 1, 0, 0);
		LocalDateTime updatedAt = now.minusDays(90); // 85일보다 이전 → 조건에 해당

		User user = mock(User.class); // User를 가짜 객체로 만듦

		when(user.getEmail()).thenReturn("test@example.com");

		UserPrivateInfo privateInfo = mock(UserPrivateInfo.class);

		when(privateInfo.getName()).thenReturn("홍길동");
		when(user.getUserPrivateInfo()).thenReturn(privateInfo);

		when(userRepository.findByUserStatusAndUpdatedAtBefore(
			eq(UserStatus.ACTIVE),
			any(LocalDateTime.class)
		)).thenReturn(List.of(user));

		// when
		userSchedulerService.testSendDeactivationWarningEmails(now);

		// then
		verify(emailService).sendDeactivationWarning(eq("test@example.com"), eq("홍길동"), any());
		verify(userRepository).findByUserStatusAndUpdatedAtBefore(eq(UserStatus.ACTIVE), any());
	}
}
