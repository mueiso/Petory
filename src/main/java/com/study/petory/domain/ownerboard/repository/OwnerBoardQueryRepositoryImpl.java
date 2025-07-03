package com.study.petory.domain.ownerboard.repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.petory.domain.ownerboard.dto.response.OwnerBoardGetAllResponseDto;
import com.study.petory.domain.ownerboard.entity.OwnerBoard;
import com.study.petory.domain.ownerboard.entity.QOwnerBoard;
import com.study.petory.domain.ownerboard.entity.QOwnerBoardImage;

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
	public Page<OwnerBoardGetAllResponseDto> findAllWithFirstImageAndTitleOptional(String title, Pageable pageable) {
		// 1. 조건 설정
		BooleanBuilder condition = new BooleanBuilder();
		condition.and(ownerBoard.deletedAt.isNull());

		if (title != null && !title.isBlank()) {
			condition.and(ownerBoard.title.contains(title));
		}

		// 2. ID 먼저 조회 (페이징 계산 용)
		List<Long> boardIds = queryFactory
			.select(ownerBoard.id)
			.from(ownerBoard)
			.where(condition)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.orderBy(ownerBoard.createdAt.desc(), ownerBoard.id.desc())
			.fetch();

		if (boardIds.isEmpty()) {
			return new PageImpl<>(Collections.emptyList(), pageable, 0L);
		}

		// 3. 실제 DTO 조회 + 첫 번째 이미지 URL
		List<OwnerBoardGetAllResponseDto> content = queryFactory
			.select(Projections.constructor(
				OwnerBoardGetAllResponseDto.class,
				ownerBoard.id,
				ownerBoard.title,
				ownerBoard.content,
				ownerBoardImage.url
			))
			.from(ownerBoard)
			.leftJoin(ownerBoardImage)
			.on(ownerBoardImage.id.eq(
				JPAExpressions
					.select(ownerBoardImage.id.min())
					.from(ownerBoardImage)
					.where(ownerBoardImage.ownerBoard.id.eq(ownerBoard.id))
			))
			.where(ownerBoard.id.in(boardIds))
			.orderBy(ownerBoard.createdAt.desc(), ownerBoard.id.desc())
			.fetch();

		// 4. 전체 개수 조회
		Long total = queryFactory
			.select(ownerBoard.count())
			.from(ownerBoard)
			.where(condition)
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
