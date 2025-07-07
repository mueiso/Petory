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

		/* [given]
		 * 현재 시각을 2025년 7월 1일 00:00으로 설정 (스케줄러 시준 시간)
		 * updatedAt 을 90일 전으로 설정 → 85일 전보다 이전이므로 조건에 해당
		 */
		LocalDateTime now = LocalDateTime.of(2025, 7, 1, 0, 0);
		LocalDateTime updatedAt = now.minusDays(90); // 85일보다 이전 → 조건에 해당

		// 테스트용 User 객체를 Mock 으로 생성
		User user = mock(User.class);

		// user.getEmail() 호출 시 "test@example.com" 반환하도록 설정
		when(user.getEmail()).thenReturn("test@example.com");

		// 내부에 있는 UserPrivateInfo 객체도 Mock 생성
		UserPrivateInfo privateInfo = mock(UserPrivateInfo.class);

		/*
		 * privateInfo.getName() 호출 시 "나이름" 반환
		 * user.getUserPrivateInfo() 호출 시 위의 privateInfo 반환
		 */
		when(privateInfo.getName()).thenReturn("나이름");
		when(user.getUserPrivateInfo()).thenReturn(privateInfo);

		/*
		 * 레포지토리에서 조건에 맞는 유저 목록 조회 시 위의 user 1건 반환
		 * 조건: UserStatus = ACTIVE, 날짜는 85일 이전
		 * List.of()로 단일 유저 리스트 반환
		 */
		when(userRepository.findByUserStatusAndUpdatedAtBefore(
			eq(UserStatus.ACTIVE),
			any(LocalDateTime.class)
		)).thenReturn(List.of(user));

		/* [when]
		 * 테스트 대상 메서드 실행: 미접속 85일 넘은 유저들에게 휴면 안내 메일 발송
		 */
		userSchedulerService.NowSendDeactivationWarningEmails(now);

		/* [then]
		 * 이메일 발송 메서드가 호풀되었는지 검증 (이메일, 이름, 날짜 확인)
		 * 레포지토리에서 ACTIVE 상태 + 날짜 조건으로 조회했는지도 검증
		 */
		verify(emailService).sendDeactivationWarning(eq("test@example.com"), eq("나이름"), any());
		verify(userRepository).findByUserStatusAndUpdatedAtBefore(eq(UserStatus.ACTIVE), any());
	}
}
