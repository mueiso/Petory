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
}
/*
일정 시작일 = 6월 이전
주 월요일 반복 6월 포함되었다면

userId
startDate


* */
