package com.study.petory.common.scheduler;

import static org.mockito.Mockito.*;

import com.study.petory.common.service.UserSchedulerService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserDeletionSchedulerTest {

	@Mock
	private UserSchedulerService userSchedulerService;

	@InjectMocks
	private UserDeletionScheduler userDeletionScheduler;

	@Test
	void sendDeletionWarningEmails_호출시_서비스메서드_실행() {

		// when: 스케줄러 메서드 직접 호출
		userDeletionScheduler.sendDeletionWarningEmails();

		// then: userSchedulerService.sendDeletionWarningEmails()가 호출되었는지 검증
		verify(userSchedulerService).sendDeletionWarningEmails();
	}

	@Test
	void hardDeleteExpiredUsers_호출시_서비스메서드_실행() {

		// when: 스케줄러 메서드 직접 호출
		userDeletionScheduler.hardDeleteExpiredUsers();

		// then: userSchedulerService.hardDeleteExpiredUsers()가 호출되었는지 검증
		verify(userSchedulerService).hardDeleteExpiredUsers();
	}
}
