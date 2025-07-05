package com.study.petory.domain.tradeboard.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.petory.domain.tradeboard.entity.QTradeBoard;
import com.study.petory.domain.tradeboard.entity.QTradeBoardImage;
import com.study.petory.domain.tradeboard.entity.TradeBoard;
import com.study.petory.domain.tradeboard.entity.TradeBoardStatus;
import com.study.petory.domain.tradeboard.entity.TradeCategory;
import com.study.petory.domain.user.entity.QUser;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TradeBoardQueryRepositoryImpl implements TradeBoardQueryRepository {

	private final JPAQueryFactory queryFactory;

	//게시글 전체 조회 시 동적쿼리 구현
	@Override
	public Page<TradeBoard> findAll(TradeCategory category, Pageable pageable) {

		QTradeBoard tradeBoard = QTradeBoard.tradeBoard;
		QTradeBoardImage tradeBoardImage = QTradeBoardImage.tradeBoardImage;

		BooleanBuilder builder = new BooleanBuilder();

		// 카테고리 필터
		if (category != null) {
			builder.and(tradeBoard.category.eq(category));
		}

		// 삭제되지 않은 게시글만
		builder.and(tradeBoard.status.ne(TradeBoardStatus.HIDDEN));

		List<TradeBoard> tradeBoards = queryFactory
			.selectFrom(tradeBoard)
			.distinct()
			.leftJoin(tradeBoard.images, tradeBoardImage).fetchJoin()
			.where(builder)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.orderBy(tradeBoard.createdAt.desc())
			.fetch();

		Long total = queryFactory
			.select(tradeBoard.count())
			.from(tradeBoard)
			.where(builder)
			.fetchOne();

		return new PageImpl<>(tradeBoards, pageable, total == null ? 0 : total);
	}

	@Override
	public Page<TradeBoard> findByUserId(Long userId, Pageable pageable) {

		QTradeBoard tradeBoard = QTradeBoard.tradeBoard;
		QTradeBoardImage tradeBoardImage = QTradeBoardImage.tradeBoardImage;
		QUser user = QUser.user;

		List<TradeBoard> content = queryFactory
			.selectFrom(tradeBoard)
			.distinct()
			.join(tradeBoard.user, user).fetchJoin()
			.leftJoin(tradeBoard.images, tradeBoardImage).fetchJoin()
			.where(user.id.eq(userId))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.orderBy(tradeBoard.createdAt.desc())
			.fetch();

		Long total = queryFactory
			.select(tradeBoard.count())
			.from(tradeBoard)
			.join(tradeBoard.user, user)
			.where(user.id.eq(userId))
			.fetchOne();

		return new PageImpl<>(content, pageable, total == null ? 0 : total);
	}
}
