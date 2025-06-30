package com.study.petory.domain.ownerBoard.repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.petory.domain.ownerBoard.entity.OwnerBoard;
import com.study.petory.domain.ownerBoard.entity.QOwnerBoard;
import com.study.petory.domain.ownerBoard.entity.QOwnerBoardImage;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class OwnerBoardQueryRepositoryImpl implements OwnerBoardQueryRepository {

	private final JPAQueryFactory queryFactory;
	private static final QOwnerBoard ownerBoard = QOwnerBoard.ownerBoard;
	private static final QOwnerBoardImage ownerBoardImage = QOwnerBoardImage.ownerBoardImage;

	private BooleanExpression notDeleted() {
		return ownerBoard.deletedAt.isNull();
	}

	private BooleanExpression isEq(Long boardId) {
		return ownerBoard.id.eq(boardId);
	}

	@Override
	public Page<OwnerBoard> findAllByTitleOptional(String title, Pageable pageable) {

		BooleanExpression condition = (title != null && !title.isBlank())
			? ownerBoard.title.contains(title).and(notDeleted()) : notDeleted();

		// 1. ID 먼저 조회
		List<Long> boardIds = queryFactory
			.select(ownerBoard.id)
			.from(ownerBoard)
			.where(condition)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		if (boardIds.isEmpty()) {
			return new PageImpl<>(Collections.emptyList(), pageable, 0L);
		}

		// 2. 실제 데이터 조회
		List<OwnerBoard> content = queryFactory
			.selectFrom(ownerBoard)
			.leftJoin(ownerBoard.images).fetchJoin()
			.where(ownerBoard.id.in(boardIds))
			.orderBy(ownerBoard.createdAt.desc(), ownerBoard.id.desc())
			.fetch();

		// 3. 총 개수 조회(구하기)
		Long total = queryFactory
			.select(ownerBoard.count())
			.from(ownerBoard)
			.fetchOne();

		return new PageImpl<>(content, pageable, total != null ? total : 0L);
	}

	@Override
	public Optional<OwnerBoard> findByIdWithImages(Long boardId) {
		OwnerBoard result = queryFactory
			.selectFrom(ownerBoard)
			.leftJoin(ownerBoard.images, ownerBoardImage).fetchJoin()
			.where(
				isEq(boardId),
				notDeleted())
			.fetchOne();

		return Optional.ofNullable(result);
	}

	@Override
	public Optional<OwnerBoard> findByIdIncludingDeleted(Long boardId) {
		OwnerBoard result = queryFactory
			.selectFrom(ownerBoard)
			.where(isEq(boardId))
			.fetchOne();

		return Optional.ofNullable(result);
	}

}
