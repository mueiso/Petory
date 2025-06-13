package com.study.petory.domain.album.repository;

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
import com.study.petory.domain.album.entity.QAlbum;
import com.study.petory.domain.album.entity.QAlbumImage;
import com.study.petory.domain.user.entity.QUser;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AlbumCustomRepositoryImpl implements AlbumCustomRepository {

	private final JPAQueryFactory jpaQueryFactory;

	private final QAlbum qAlbum = QAlbum.album;

	private final QAlbumImage qAlbumImage = QAlbumImage.albumImage;

	private final QUser qUser = QUser.user;

	public Page<Album> findAllAlbum(Long userId, Pageable pageable) {
		BooleanBuilder builder = new BooleanBuilder();
		if (userId != null) {
			builder.and(qAlbum.user.id.eq(userId));
		}

		JPAQuery<Album> query = jpaQueryFactory
			.selectFrom(qAlbum)
			.join(qUser).on(qUser.id.eq(qAlbum.user.id)).fetchJoin()
			/*
			leftJoin(A.field, B)
			B가 A를 조인하고 있으며 A.field 의 필드와 같은 타입
			 */
			.leftJoin(qAlbum.albumImageList, qAlbumImage).fetchJoin()
			.where(
				qAlbumImage.id.eq(
					JPAExpressions
						.select(qAlbumImage.id.min())
						.from(qAlbumImage)
						.where(qAlbumImage.album.id.eq(qAlbum.id))
				),
				builder
			)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize());

		// new PathBuilder(qAlbum.getType(), qAlbum.getMetadata()); 와 동일
		/*
		qAlbum.getType() → Album.class 를 반환
		qAlbum.getMetadata() → "album" 별칭을 반환하기에 동일
		 */
		PathBuilder pathBuilder = new PathBuilder<>(Album.class, "album");
		for (Sort.Order o : pageable.getSort()) {
			query.orderBy(new OrderSpecifier(o.isAscending() ? Order.ASC : Order.DESC,
				pathBuilder.get(o.getProperty())));
		}

		Long total = jpaQueryFactory
			.select(qAlbum.count())
			.from(qAlbum)
			.where(builder)
			.fetchOne();

		if (total == null) {
			total = 0L;
		}

		List<Album> albumList = query.fetch();
		return new PageImpl<>(albumList, pageable, total);
	}

	public Optional<Album> findOneAlbumByUser(Long albumId) {
		return Optional.ofNullable(
			jpaQueryFactory
				.selectFrom(qAlbum)
				.distinct()
				.leftJoin(qAlbum.albumImageList, qAlbumImage)
				.fetchJoin()
				.where(qAlbum.id.eq(albumId))
				.fetchOne()
		);
	}
}
