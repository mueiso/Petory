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
	private UserSchedulerService userSchedulerService;

	@InjectMocks
	private UserDeactivationScheduler userDeactivationScheduler;

	@Test
	void sendDeactivationWarningEmails_호출시_서비스메서드_실행() {

		/* [when]
		 * 테스트 대상 메서드 호출
		 * 내부적으로 userSchedulerService.sendDeactivationWarningEmails() 호출
		 */
		userDeactivationScheduler.sendDeactivationWarningEmails();

		/* [then]
		 * userSchedulerService 의 해당 메서드가 정확히 호출됐는지 검증
		 */
		verify(userSchedulerService).sendDeactivationWarningEmails();
	}

	@Test
	void deactivateInactiveUsers_호출시_서비스메서드_실행() {

		/* [when]
		 * 테스트 대상 메서드 호출
		 * 내부적으로 userSchedulerService.deactivateInactiveUsers() 실행
		 */
		userDeactivationScheduler.deactivateInactiveUsers();

		/* [then]
		 * userSchedulerService 의 해당 메서드가 정확히 호출됐는지 검증
		 */
		verify(userSchedulerService).deactivateInactiveUsers();
	}
}
