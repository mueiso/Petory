package com.study.petory.domain.notification.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.petory.domain.notification.entity.Notification;
import com.study.petory.domain.notification.entity.QNotification;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class NotificationQueryRepositoryImpl implements NotificationQueryRepository{

	private final JPAQueryFactory queryFactory;

	@Override
	public Page<Notification> findByUserId(Long userId, Pageable pageable) {

		QNotification notification = QNotification.notification;

		List<Notification> notifications = queryFactory
			.selectFrom(notification)
			.where(notification.user.id.eq(userId))
			.orderBy(notification.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long total = queryFactory
			.select(notification.count())
			.from(notification)
			.where(notification.user.id.eq(userId))
			.fetchOne();

		return new PageImpl<>(notifications, pageable, total != null ? total : 0);
	}
}
