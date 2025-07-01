package com.study.petory.common.scheduler;

import static org.mockito.Mockito.*;

import com.study.petory.common.service.UserSchedulerService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserDeactivationSchedulerTest {

	@Mock
	private UserSchedulerService userSchedulerService; // 내부 서비스 모킹

	@InjectMocks
	private UserDeactivationScheduler userDeactivationScheduler; // 테스트 대상 클래스

	@Test
	void sendDeactivationWarningEmails_호출시_서비스메서드_실행() {

		// when
		userDeactivationScheduler.sendDeactivationWarningEmails();

		// then
		verify(userSchedulerService).sendDeactivationWarningEmails(); // 호출 여부 검증
	}

	@Test
	void deactivateInactiveUsers_호출시_서비스메서드_실행() {

		// when
		userDeactivationScheduler.deactivateInactiveUsers();

		// then
		verify(userSchedulerService).deactivateInactiveUsers(); // 호출 여부 검증
	}
}
