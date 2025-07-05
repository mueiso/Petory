package com.study.petory.common.util;

import static org.assertj.core.api.Assertions.*;

import java.time.Duration;
import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
public class CustomDateUtilTest {

	@Test
	@DisplayName("현재 날짜를 MM-dd 형식으로 반환")
	public void testGetFormatDate() {
		// given
		LocalDateTime nowTime = LocalDateTime.of(2025, 7, 1, 14, 30, 0);
		try (MockedStatic<LocalDateTime> mockedLocalDateTime = mockStatic(LocalDateTime.class)) {
			mockedLocalDateTime.when(LocalDateTime::now).thenReturn(nowTime);

			// when
			String result = CustomDateUtil.getFormatDate();

			// then
			assertThat(result).isEqualTo("07-01");
		}
	}

	@Test
	@DisplayName("오늘 하루의 남은 시간을 반환")
	public void testRemainderTime() {
		// given
		LocalDateTime nowTime = LocalDateTime.of(2025, 1, 1, 14, 30, 0);
		try (MockedStatic<LocalDateTime> mockedLocalDateTime = mockStatic(LocalDateTime.class)) {
			mockedLocalDateTime.when(LocalDateTime::now).thenReturn(nowTime);

			// when
			Duration result = CustomDateUtil.remainderTime();

			// then
			LocalDateTime lastTime = LocalDateTime.of(2025, 7, 1, 23,59,59, 999999999);
			Duration expected = Duration.between(nowTime, lastTime);
			assertThat(result).isEqualTo(expected);
		}
	}
}
