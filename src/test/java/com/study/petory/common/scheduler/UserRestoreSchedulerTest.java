package com.study.petory.common.scheduler;

import static org.mockito.Mockito.*;

import com.study.petory.common.service.UserSchedulerService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserRestoreSchedulerTest {

	@Mock
	private UserSchedulerService userSchedulerService;

	@InjectMocks
	private UserRestoreScheduler userRestoreScheduler;

	@Test
	void restoreSuspendedUsers_호출시_서비스메서드_실행() {

		/* [when]
		 * 테스트 대상인 restoreSuspendedUsers() 메서드 직접 호출
		 * 스케줄링 없이 수동으로 실행
		 */
		userRestoreScheduler.restoreSuspendedUsers();

		/* [then]
		 * 내부적으로 userSchedulerService.restoreSuspendedUsers()가 호출되었는지 검증
		 */
		verify(userSchedulerService).restoreSuspendedUsers();
	}
}
