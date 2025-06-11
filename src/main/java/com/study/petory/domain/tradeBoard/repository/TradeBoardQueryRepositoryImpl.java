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

	//게시글 전체 조회 시 동적쿼리 구현
	@Override
	public Page<TradeBoard> findAll(TradeCategory category, Pageable pageable) {

		QTradeBoard tradeBoard = QTradeBoard.tradeBoard;

		BooleanBuilder builder = new BooleanBuilder();

		if (category != null) { //카테고리가 null이 아니라면 category별로 조회
			builder.and(tradeBoard.category.eq(category));
		}

		builder.and(tradeBoard.status.ne(TradeBoardStatus.HIDDEN)); //게시글의 상태가 HIDDEN 일 경우 조회 X

		List<TradeBoard> tradeBoards = queryFactory
			.selectFrom(tradeBoard)
			.where(builder)
			.orderBy(tradeBoard.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageNumber())
			.fetch();

		//Page로 만들기위해 전체 페이지 계산
		Long total = queryFactory
			.select(tradeBoard.count())
			.from(tradeBoard)
			.where(builder)
			.fetchOne();

		return new PageImpl<>(tradeBoards, pageable, total == null ? 0 : total);
	}
}
