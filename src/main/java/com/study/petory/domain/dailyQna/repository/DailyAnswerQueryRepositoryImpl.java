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
import com.study.petory.domain.dailyQna.entity.DailyAnswer;
import com.study.petory.domain.dailyQna.entity.DailyAnswerStatus;
import com.study.petory.domain.dailyQna.entity.QDailyAnswer;
import com.study.petory.domain.dailyQna.entity.QDailyQuestion;
import com.study.petory.domain.user.entity.QUser;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class DailyAnswerQueryRepositoryImpl implements DailyAnswerQueryRepository {

	private final JPAQueryFactory jpaQueryFactory;

	private final QDailyAnswer qDailyAnswer = QDailyAnswer.dailyAnswer;

	private final QUser qUser = QUser.user;

	private final QDailyQuestion qDailyQuestion = QDailyQuestion.dailyQuestion;

	private final PathBuilder pathBuilder = new PathBuilder<>(DailyAnswer.class, "dailyAnswer");

	@Override
	public boolean isDailyAnswerToday(Long userId, Long dailyQuestionId) {
		LocalDate today = LocalDate.now();
		return jpaQueryFactory
			.selectOne()
			.from(qDailyAnswer)
			.where(
				qDailyAnswer.user.id.eq(userId),
				qDailyAnswer.dailyQuestion.id.eq(dailyQuestionId),
				qDailyAnswer.createdAt.between(
					today.atStartOfDay(),
					today.plusDays(1).atStartOfDay().minusNanos(1)
				)
			)
			.fetchFirst() != null;
	}

	@Override
	public Optional<DailyAnswer> findDailyAnswerByStatusAndId(List<DailyAnswerStatus> statusList, Long dailyAnswerId) {
		BooleanBuilder status = new BooleanBuilder();

		if (statusList.contains(DailyAnswerStatus.ACTIVE)) {
			status.or(qDailyAnswer.dailyAnswerStatus.eq(DailyAnswerStatus.ACTIVE));
		}
		if (statusList.contains(DailyAnswerStatus.HIDDEN)) {
			status.or(qDailyAnswer.dailyAnswerStatus.eq(DailyAnswerStatus.HIDDEN));
		}
		if (statusList.contains(DailyAnswerStatus.DELETED)) {
			status.or(qDailyAnswer.dailyAnswerStatus.eq(DailyAnswerStatus.DELETED));
		}

		return Optional.ofNullable(
			jpaQueryFactory
				.selectFrom(qDailyAnswer)
				.where(
					qDailyAnswer.id.eq(dailyAnswerId),
					status
				)
				.fetchFirst()
		);
	}

	@Override
	public List<DailyAnswer> findDailyAnswer(Long userId, Long dailyQuestionId) {
		return jpaQueryFactory
			.selectFrom(qDailyAnswer)
			.join(qUser).on(qUser.id.eq(qDailyAnswer.user.id)).fetchJoin()
			.join(qDailyQuestion).on(qDailyQuestion.id.eq(qDailyAnswer.dailyQuestion.id)).fetchJoin()
			.where(
				qDailyAnswer.user.id.eq(userId),
				qDailyAnswer.dailyQuestion.id.eq(dailyQuestionId),
				qDailyAnswer.dailyAnswerStatus.eq(DailyAnswerStatus.ACTIVE)
			)
			.orderBy(qDailyAnswer.createdAt.desc())
			.fetch();
	}

	@Override
	public Page<DailyAnswer> findDailyAnswerPageByStatus(List<DailyAnswerStatus> statusList, Long userId,
		Pageable pageable) {
		BooleanBuilder status = new BooleanBuilder();

		if (statusList.contains(DailyAnswerStatus.ACTIVE)) {
			status.or(qDailyAnswer.dailyAnswerStatus.eq(DailyAnswerStatus.ACTIVE));
		}
		if (statusList.contains(DailyAnswerStatus.HIDDEN)) {
			status.or(qDailyAnswer.dailyAnswerStatus.eq(DailyAnswerStatus.HIDDEN));
		}
		if (statusList.contains(DailyAnswerStatus.DELETED)) {
			status.or(qDailyAnswer.dailyAnswerStatus.eq(DailyAnswerStatus.DELETED));
		}

		JPAQuery<DailyAnswer> query = jpaQueryFactory
			.selectFrom(qDailyAnswer)
			.join(qDailyAnswer.user, qUser).fetchJoin()
			.where(
				qDailyAnswer.user.id.eq(userId),
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
			.select(qDailyAnswer.count())
			.from(qDailyAnswer)
			.where(
				qDailyAnswer.user.id.eq(userId),
				status
			)
			.fetchOne();
		List<DailyAnswer> dailyAnswerList = query.fetch();
		return new PageImpl<>(dailyAnswerList, pageable, total);
	}

	@Override
	public Optional<DailyAnswerStatus> findDailyAnswerStatusById(Long dailyAnswerId) {
		return Optional.ofNullable(
			jpaQueryFactory
				.select(qDailyAnswer.dailyAnswerStatus)
				.from(qDailyAnswer)
				.where(
					qDailyAnswer.id.eq(dailyAnswerId)
				)
				.fetchFirst()
		);
	}
}
