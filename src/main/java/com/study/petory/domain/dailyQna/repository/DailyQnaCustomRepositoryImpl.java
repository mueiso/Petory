package com.study.petory.domain.dailyQna.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.petory.domain.dailyQna.entity.DailyQna;
import com.study.petory.domain.dailyQna.entity.DailyQnaStatus;
import com.study.petory.domain.dailyQna.entity.QDailyQna;
import com.study.petory.domain.dailyQna.entity.QQuestion;
import com.study.petory.domain.user.entity.QUser;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class DailyQnaCustomRepositoryImpl implements DailyQnaCustomRepository {

	private final JPAQueryFactory jpaQueryFactory;

	private final QDailyQna qDailyQna = QDailyQna.dailyQna;

	private final QUser qUser = QUser.user;

	private final QQuestion qQuestion = QQuestion.question;

	private final PathBuilder pathBuilder = new PathBuilder<>(DailyQna.class, "dailyQna");

	@Override
	public boolean isDailyQnaToday(Long userId, Long questionId) {
		LocalDate today = LocalDate.now();
		return jpaQueryFactory
			.selectOne()
			.from(qDailyQna)
			.where(
				qDailyQna.user.id.eq(userId),
				qDailyQna.question.id.eq(questionId),
				qDailyQna.createdAt.between(
					today.atStartOfDay(),
					today.plusDays(1).atStartOfDay().minusNanos(1)
				)
			)
			.fetchFirst() != null;
	}

	@Override
	public Optional<DailyQna> findDailyQnaByStatusAndId(List<DailyQnaStatus> statusList, Long dailyQnaId) {
		BooleanBuilder status = new BooleanBuilder();

		if (statusList.contains(DailyQnaStatus.ACTIVE)) {
			status.or(qDailyQna.dailyQnaStatus.eq(DailyQnaStatus.ACTIVE));
		}
		if (statusList.contains(DailyQnaStatus.HIDDEN)) {
			status.or(qDailyQna.dailyQnaStatus.eq(DailyQnaStatus.HIDDEN));
		}
		if (statusList.contains(DailyQnaStatus.DELETED)) {
			status.or(qDailyQna.dailyQnaStatus.eq(DailyQnaStatus.DELETED));
		}

		return Optional.ofNullable(
			jpaQueryFactory
				.selectFrom(qDailyQna)
				.where(
					qDailyQna.id.eq(dailyQnaId),
					status
				)
				.fetchFirst()
		);
	}

	@Override
	public List<DailyQna> findDailyQna(Long userId, Long questionId) {
		return jpaQueryFactory
			.selectFrom(qDailyQna)
			.join(qUser).on(qUser.id.eq(qDailyQna.user.id)).fetchJoin()
			.join(qQuestion).on(qQuestion.id.eq(qDailyQna.question.id)).fetchJoin()
			.where(
				qDailyQna.user.id.eq(userId),
				qDailyQna.question.id.eq(questionId),
				qDailyQna.dailyQnaStatus.eq(DailyQnaStatus.ACTIVE)
			)
			.orderBy(qDailyQna.createdAt.desc())
			.fetch();
	}

	@Override
	public Page<DailyQna> findDailyQnaPageByStatus(List<DailyQnaStatus> statusList, Long userId, Pageable pageable) {
		BooleanBuilder status = new BooleanBuilder();

		if (statusList.contains(DailyQnaStatus.ACTIVE)) {
			status.or(qDailyQna.dailyQnaStatus.eq(DailyQnaStatus.ACTIVE));
		}
		if (statusList.contains(DailyQnaStatus.HIDDEN)) {
			status.or(qDailyQna.dailyQnaStatus.eq(DailyQnaStatus.HIDDEN));
		}
		if (statusList.contains(DailyQnaStatus.DELETED)) {
			status.or(qDailyQna.dailyQnaStatus.eq(DailyQnaStatus.DELETED));
		}

		JPAQuery<DailyQna> query = jpaQueryFactory
			.selectFrom(qDailyQna)
			.join(qDailyQna.user, qUser).fetchJoin()
			.where(
				qDailyQna.user.id.eq(userId),
				status
			)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize());

		for (Sort.Order o : pageable.getSort()) {
			query.orderBy(new OrderSpecifier(o.isAscending() ? Order.ASC : Order.DESC,
				pathBuilder.get(o.getProperty()))
			);
		}

		Long total = jpaQueryFactory
			.select(qDailyQna.count())
			.from(qDailyQna)
			.where(
				qDailyQna.user.id.eq(userId),
				status
			)
			.fetchOne();
		List<DailyQna> dailyQnaList = query.fetch();
		return new PageImpl<>(dailyQnaList, pageable, total);
	}

	@Override
	public Optional<DailyQnaStatus> findDailyQnaStatusById(Long dailyQnaId) {
		return Optional.ofNullable(
			jpaQueryFactory
				.select(qDailyQna.dailyQnaStatus)
				.from(qDailyQna)
				.where(
					qDailyQna.id.eq(dailyQnaId)
				)
				.fetchFirst()
		);
	}
}
