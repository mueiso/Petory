package com.study.petory.domain.album.repository;

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
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.petory.domain.album.entity.Album;
import com.study.petory.domain.album.entity.AlbumVisibility;
import com.study.petory.domain.album.entity.QAlbum;
import com.study.petory.domain.album.entity.QAlbumImage;
import com.study.petory.domain.user.entity.QUser;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AlbumQueryRepositoryImpl implements AlbumQueryRepository {

	private final JPAQueryFactory jpaQueryFactory;

	private final QAlbum qAlbum = QAlbum.album;

	private final QAlbumImage qAlbumImage = QAlbumImage.albumImage;

	private final QUser qUser = QUser.user;

	@Override
	public Page<Album> findAllAlbum(boolean showOnlyPublic, Long userId, Pageable pageable) {
		BooleanBuilder isPublic = new BooleanBuilder();
		if (showOnlyPublic) {
			isPublic.and(qAlbum.albumVisibility.eq(AlbumVisibility.PUBLIC));
		}

		BooleanBuilder builder = new BooleanBuilder();
		if (userId != null) {
			builder.and(qAlbum.user.id.eq(userId));
		}
		if (userId == null) {
			builder.and(qAlbum.albumVisibility.eq(AlbumVisibility.PUBLIC));
		}

		JPAQuery<Album> query = jpaQueryFactory
			.selectFrom(qAlbum)
			.join(qUser).on(qUser.id.eq(qAlbum.user.id)).fetchJoin()
			.leftJoin(qAlbum.albumImageList, qAlbumImage).fetchJoin()
			.where(
				builder,
				qAlbumImage.id.eq(
					JPAExpressions
						.select(qAlbumImage.id.min())
						.from(qAlbumImage)
						.where(qAlbumImage.album.id.eq(qAlbum.id))
				),
				isPublic
			)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize());

		PathBuilder pathBuilder = new PathBuilder<>(Album.class, "album");
		for (Sort.Order o : pageable.getSort()) {
			query.orderBy(new OrderSpecifier(o.isAscending() ? Order.ASC : Order.DESC,
				pathBuilder.get(o.getProperty())));
		}

		Long total = jpaQueryFactory
			.select(qAlbum.count())
			.from(qAlbum)
			.where(
				builder,
				isPublic
			)
			.fetchOne();

		if (total == null) {
			total = 0L;
		}

		List<Album> albumList = query.fetch();
		return new PageImpl<>(albumList, pageable, total);
	}

	@Override
	public Optional<Album> findOneAlbumByUser(boolean showOnlyPublic, Long albumId) {
		BooleanBuilder isPublic = new BooleanBuilder();
		if (showOnlyPublic) {
			isPublic.and(qAlbum.albumVisibility.eq(AlbumVisibility.PUBLIC));
		}
		return Optional.ofNullable(
			jpaQueryFactory
				.selectFrom(qAlbum)
				.distinct()
				.leftJoin(qAlbum.albumImageList, qAlbumImage)
				.fetchJoin()
				.where(
					qAlbum.id.eq(albumId),
					isPublic
				)
				.fetchOne()
		);
	}

	@Override
	public boolean existTodayAlbum(Long userId) {
		LocalDate today = LocalDate.now();
		return jpaQueryFactory
			.selectOne()
			.from(qAlbum)
			.join(qUser).on(qUser.id.eq(qAlbum.user.id)).fetchJoin()
			.where(
				qAlbum.user.id.eq(userId),
				qAlbum.createdAt.between(
					today.atStartOfDay(),
					today.plusDays(1).atStartOfDay().minusNanos(1)
				)
			)
			.fetchFirst() != null;
	}

	@Override
	public boolean isUserAlbum(Long userId, Long albumId) {
		return jpaQueryFactory
			.selectOne()
			.from(qAlbum)
			.join(qUser).on(qUser.id.eq(qAlbum.user.id)).fetchJoin()
			.where(
				qAlbum.id.eq(albumId),
				qAlbum.user.id.eq(userId)
			)
			.fetchFirst() != null;
	}
}
