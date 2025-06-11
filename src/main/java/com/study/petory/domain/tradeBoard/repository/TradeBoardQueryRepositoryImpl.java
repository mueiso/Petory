package com.study.petory.domain.tradeBoard.repository;

import java.util.List;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.petory.domain.tradeBoard.entity.QTradeBoard;
import com.study.petory.domain.tradeBoard.entity.TradeBoard;
import com.study.petory.domain.tradeBoard.entity.TradeBoardStatus;
import com.study.petory.domain.tradeBoard.entity.TradeCategory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TradeBoardQueryRepositoryImpl implements TradeBoardQueryRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public Page<TradeBoard> findAll(TradeCategory category, Pageable pageable) {

		QTradeBoard tradeBoard = QTradeBoard.tradeBoard;

		BooleanBuilder builder = new BooleanBuilder();

		if (category != null) {
			builder.and(tradeBoard.category.eq(category));
		}

		builder.and(tradeBoard.status.ne(TradeBoardStatus.HIDDEN));

		List<TradeBoard> tradeBoards = queryFactory
			.selectFrom(tradeBoard)
			.where(builder)
			.orderBy(tradeBoard.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageNumber())
			.fetch();

		Long total = queryFactory
			.select(tradeBoard.count())
			.from(tradeBoard)
			.where(builder)
			.fetchOne();

		return new PageImpl<>(tradeBoards, pageable, total == null ? 0 : total);
	}
}
