package com.study.petory.domain.event.repository;

import java.time.LocalDateTime;
import java.util.List;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.petory.domain.event.entity.Event;
import com.study.petory.domain.event.entity.QEvent;
import com.study.petory.domain.user.entity.QUser;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EventQueryRepositoryImpl implements EventQueryRepository {

	private final JPAQueryFactory jpaQueryFactory;

	private final QEvent qEvent = QEvent.event;

	private final QUser qUser = QUser.user;

	public List<Event> findEventListStart(Long userId, LocalDateTime start, LocalDateTime end) {
		return jpaQueryFactory
			.selectFrom(qEvent)
			.join(qUser).on(qUser.id.eq(qEvent.user.id)).fetchJoin()
			.where(
				qEvent.user.id.eq(userId),
				qEvent.startDate.between(start, end)
					.or(qEvent.startDate.lt(start)
						.and(qEvent.rrule.isNotNull())
						.and(qEvent.recurrenceEnd.isNull()
							.or(qEvent.recurrenceEnd.gt(start))
						)
					)
			)
			.orderBy(qEvent.startDate.asc())
			.stream().toList();
	}
}