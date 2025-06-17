package com.study.petory.domain.dailyQna.repository;

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
import com.study.petory.domain.dailyQna.entity.QQuestion;
import com.study.petory.domain.dailyQna.entity.Question;
import com.study.petory.domain.dailyQna.entity.QuestionStatus;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class QuestionCustomRepositoryImpl implements QuestionCustomRepository {

	private final JPAQueryFactory jpaQueryFactory;

	private final QQuestion qQuestion = QQuestion.question;

	private final PathBuilder pathBuilder = new PathBuilder<>(Question.class, "question");

	@Override
	public boolean existsByDate(String date) {
		return jpaQueryFactory
			.selectOne()
			.from(qQuestion)
			.where(
				qQuestion.date.eq(date)
			)
			.fetchFirst() != null;
	}

	@Override
	public Page<Question> findQuestionPageByStatus(List<QuestionStatus> statusList, Pageable pageable) {
		BooleanBuilder status = new BooleanBuilder();
		if (statusList.contains(QuestionStatus.ACTIVE)) {
			status.or(qQuestion.questionStatus.eq(QuestionStatus.ACTIVE));
		}
		if (statusList.contains(QuestionStatus.INACTIVE)) {
			status.or(qQuestion.questionStatus.eq(QuestionStatus.INACTIVE));
		}
		if (statusList.contains(QuestionStatus.DELETED)) {
			status.or(qQuestion.questionStatus.eq(QuestionStatus.DELETED));
		}

		JPAQuery<Question> query = jpaQueryFactory
			.selectFrom(qQuestion)
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
			.select(qQuestion.count())
			.from(qQuestion)
			.where(
				status
			)
			.fetchOne();

		// 오류 발생 위치
		List<Question> questionList = query.fetch();

		return new PageImpl<>(questionList, pageable, total);
	}

	@Override
	public Optional<Question> findTodayQuestion(String date) {
		return Optional.ofNullable(
			jpaQueryFactory
				.selectFrom(qQuestion)
				.where(
					qQuestion.date.eq(date),
					qQuestion.questionStatus.eq(QuestionStatus.ACTIVE)
				)
				.fetchFirst()
		);
	}

	@Override
	public Optional<Question> findQuestionByStatusAndId(List<QuestionStatus> statusList, Long questionId) {
		BooleanBuilder status = new BooleanBuilder();
		if (statusList.contains(QuestionStatus.ACTIVE)) {
			status.or(qQuestion.questionStatus.eq(QuestionStatus.ACTIVE));
		}
		if (statusList.contains(QuestionStatus.INACTIVE)) {
			status.or(qQuestion.questionStatus.eq(QuestionStatus.INACTIVE));
		}
		if (statusList.contains(QuestionStatus.DELETED)) {
			status.or(qQuestion.questionStatus.eq(QuestionStatus.DELETED));
		}
		return Optional.ofNullable(
			jpaQueryFactory
				.selectFrom(qQuestion)
				.where(
					qQuestion.id.eq(questionId),
					status
				)
				.fetchFirst()
		);
	}

	@Override
	public Optional<QuestionStatus> findQuestionStatusById(Long questionId) {
		return Optional.ofNullable(
			jpaQueryFactory
				.select(qQuestion.questionStatus)
				.from(qQuestion)
				.where(
					qQuestion.id.eq(questionId)
				)
				.fetchFirst()
		);
	}
}
