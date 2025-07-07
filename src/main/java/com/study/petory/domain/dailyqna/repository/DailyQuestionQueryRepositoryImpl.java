package com.study.petory.domain.dailyqna.repository;

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
import com.study.petory.domain.dailyqna.entity.DailyQuestion;
import com.study.petory.domain.dailyqna.entity.DailyQuestionStatus;
import com.study.petory.domain.dailyqna.entity.QDailyQuestion;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class DailyQuestionQueryRepositoryImpl implements DailyQuestionQueryRepository {

	private final JPAQueryFactory jpaQueryFactory;

	private final QDailyQuestion qDailyQuestion = QDailyQuestion.dailyQuestion;

	private final PathBuilder pathBuilder = new PathBuilder<>(DailyQuestion.class, "dailyQuestion");

	@Override
	public boolean existsByDate(String date) {
		return jpaQueryFactory
			.selectOne()
			.from(qDailyQuestion)
			.where(
				qDailyQuestion.date.eq(date)
			)
			.fetchFirst() != null;
	}

	@Override
	public Page<DailyQuestion> findDailyQuestionPageByStatus(List<DailyQuestionStatus> statusList, Pageable pageable) {
		BooleanBuilder status = new BooleanBuilder();
		if (statusList.contains(DailyQuestionStatus.ACTIVE)) {
			status.or(qDailyQuestion.dailyQuestionStatus.eq(DailyQuestionStatus.ACTIVE));
		}
		if (statusList.contains(DailyQuestionStatus.INACTIVE)) {
			status.or(qDailyQuestion.dailyQuestionStatus.eq(DailyQuestionStatus.INACTIVE));
		}
		if (statusList.contains(DailyQuestionStatus.DELETED)) {
			status.or(qDailyQuestion.dailyQuestionStatus.eq(DailyQuestionStatus.DELETED));
		}

		JPAQuery<DailyQuestion> query = jpaQueryFactory
			.selectFrom(qDailyQuestion)
			.where(
				status
			)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize());

		for (Sort.Order o : pageable.getSort()) {
			query.orderBy(new OrderSpecifier(o.isAscending() ? Order.ASC : Order.DESC,
				pathBuilder.get(o.getProperty())));
		}

		Long total = jpaQueryFactory
			.select(qDailyQuestion.count())
			.from(qDailyQuestion)
			.where(
				status
			)
			.fetchOne();

		List<DailyQuestion> dailyQuestionList = query.fetch();

		return new PageImpl<>(dailyQuestionList, pageable, total);
	}

	@Override
	public Optional<DailyQuestion> findTodayDailyQuestion(String date) {
		return Optional.ofNullable(
			jpaQueryFactory
				.selectFrom(qDailyQuestion)
				.where(
					qDailyQuestion.date.eq(date),
					qDailyQuestion.dailyQuestionStatus.eq(DailyQuestionStatus.ACTIVE)
				)
				.fetchFirst()
		);
	}

	@Override
	public Optional<DailyQuestion> findDailyQuestionByStatusAndId(List<DailyQuestionStatus> statusList, Long dailyQuestionId) {
		BooleanBuilder status = new BooleanBuilder();
		if (statusList.contains(DailyQuestionStatus.ACTIVE)) {
			status.or(qDailyQuestion.dailyQuestionStatus.eq(DailyQuestionStatus.ACTIVE));
		}
		if (statusList.contains(DailyQuestionStatus.INACTIVE)) {
			status.or(qDailyQuestion.dailyQuestionStatus.eq(DailyQuestionStatus.INACTIVE));
		}
		if (statusList.contains(DailyQuestionStatus.DELETED)) {
			status.or(qDailyQuestion.dailyQuestionStatus.eq(DailyQuestionStatus.DELETED));
		}
		return Optional.ofNullable(
			jpaQueryFactory
				.selectFrom(qDailyQuestion)
				.where(
					qDailyQuestion.id.eq(dailyQuestionId),
					status
				)
				.fetchFirst()
		);
	}

	@Override
	public Optional<DailyQuestionStatus> findDailyQuestionStatusById(Long dailyQuestionId) {
		return Optional.ofNullable(
			jpaQueryFactory
				.select(qDailyQuestion.dailyQuestionStatus)
				.from(qDailyQuestion)
				.where(
					qDailyQuestion.id.eq(dailyQuestionId)
				)
				.fetchFirst()
		);
	}
}
