package com.study.petory.domain.event.repository;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.study.petory.common.config.QueryDSLConfig;
import com.study.petory.common.util.CustomDateUtil;
import com.study.petory.domain.event.entity.Event;
import com.study.petory.domain.event.entity.EventColor;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.entity.UserPrivateInfo;
import com.study.petory.domain.user.repository.UserRepository;

@DataJpaTest
@Import(QueryDSLConfig.class)
@ActiveProfiles("test")
public class EventRepositoryTest {

	@Autowired
	private EventRepository eventRepository;

	@Autowired
	private UserRepository userRepository;

	private Long userId;

	private final UserPrivateInfo userInfo = new UserPrivateInfo(
		"1L",
		"홍길동",
		"000-0000-0000"
	);

	private final User user = User.builder()
		.email("test@email.com")
		.nickname("길동이")
		.userPrivateInfo(userInfo)
		.build();

	@BeforeEach
	public void setUp() {
		userRepository.save(user);
		userId = user.getId();
	}

	private void setEvent(String start, String end, boolean isAllDay, String rrule, LocalDateTime recurrenceEnd,
		String rDate, String exDate) {
		eventRepository.save(Event.builder()
			.user(user)
			.title("일정 제목")
			.startDate(CustomDateUtil.stringToLocalDateTime(start))
			.endDate(CustomDateUtil.stringToLocalDateTime(end))
			.timeZone("Asia/Seoul")
			.isAllDay(isAllDay)
			.rrule(rrule)
			.recurrenceEnd(recurrenceEnd)
			.rDate(rDate)
			.exDate(exDate)
			.description(null)
			.color(EventColor.SAGE)
			.build()
		);
	}

	@Test
	@DisplayName("기간 안에 있는 일정과 기간 안에 들어올 가능성이 있는 반복 조회를 모두 조회한다.")
	public void testFindEventListStart() {
		// given
		LocalDateTime start = LocalDateTime.parse("2025-06-29T00:00:00");
		LocalDateTime end = LocalDateTime.parse("2025-08-10T00:00:00");

		setEvent(
			"2025-07-03T09:00:00",
			"2025-07-03T09:00:00",
			true,
			"RRULE:FREQ=DAILY;INTERVAL=2;COUNT=3",
			null,
			"20250709T000000Z",
			"20250705T000000Z"
		);

		setEvent(
			"2025-06-18T00:00:00",
			"2025-06-18T00:00:00",
			true,
			"RRULE:FREQ=WEEKLY;UNTIL:20250812T000000Z",
			LocalDateTime.of(2025, 8, 12, 0, 0, 0),
			"20250717T000000Z",
			"20250716T000000Z"
		);

		// when
		List<Event> response = eventRepository.findEventListStart(userId, start, end);

		// then
		assertThat(response.size()).isEqualTo(2);
		assertThat(response.get(0).getStartDate()).isEqualTo("2025-06-18T00:00:00");
		assertThat(response.get(1).getStartDate()).isEqualTo("2025-07-03T09:00:00");
	}
}
