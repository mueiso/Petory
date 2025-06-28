package com.study.petory.domain.calendar.repository;

import java.time.LocalDateTime;
import java.util.List;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.petory.domain.calendar.entity.Event;
import com.study.petory.domain.calendar.entity.QEvent;
import com.study.petory.domain.user.entity.QUser;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EventQueryRepositoryImpl implements EventQueryRepository {

	private final JPAQueryFactory jpaQueryFactory;

	private final QEvent qEvent = QEvent.event;

	private final QUser qUser = QUser.user;

	// @Override
	public List<Event> findEventList(Long userId, LocalDateTime start, LocalDateTime end) {
		return jpaQueryFactory
			.selectFrom(qEvent)
			.join(qUser).on(qUser.id.eq(qEvent.user.id)).fetchJoin()
			.where(
				qEvent.user.id.eq(userId),
				qEvent.startDate.between(start, end)
				// todo 반복 일정 조회 필요
			)
			.orderBy(qEvent.startDate.asc())
			.stream().toList();
	}

	// start: 조회 범위 시작일, end: 조회 범위 종료일
	public List<Event> findEventListStart(Long userId, LocalDateTime start, LocalDateTime end) {
		return jpaQueryFactory
			.selectFrom(qEvent)
			.join(qUser).on(qUser.id.eq(qEvent.user.id)).fetchJoin()
			.where(
				qEvent.user.id.eq(userId),
				// startDate: 일정 시작일
				qEvent.startDate.between(start, end)
					// 일정 시작일이 조회 범위 전인 일정만 조회
					// 일정 시작일 > 조회 시작일
					.or(qEvent.startDate.lt(start)
						// rrule: 반복 조건
						.and(qEvent.rrule.isNotNull())
						// recurrenceEnd: 반복 종료일
						.and(qEvent.recurrenceEnd.isNull()
							// 반복 종료일이 시작일 이후에 종료 되는 일정
							.or(qEvent.recurrenceEnd.gt(start))
						)
					)
			)
			.orderBy(qEvent.startDate.asc())
			.stream().toList();
	}
}