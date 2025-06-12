package com.study.petory.domain.album.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.jpa.JPAExpressions;
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

	public Page<Album> getAll(Long userId, Pageable pageable) {
		List<Album> albumList = jpaQueryFactory
			.select(
				qAlbum.id,
				qAlbum.user,
				qAlbum.content,
				qAlbum.albumVisibility,
				qAlbum.createdAt,
				qAlbum.updatedAt,
				qAlbum.deletedAt,
				qAlbum.albumImageList
			)
			.from(qAlbum)
			.join(qUser).on(qUser.id.eq(qAlbum.user.id)).fetchJoin()
			.where(
				JPAExpressions
					.select()
			)
	}
	/**
	 * select *
	 * from album a
	 * left join album_image i on a.id = i.id
	 * where a.created_at = (
	 * 		select min(created_at)
	 * 		from album_image
	 *		where a.id = b.id
	 *
	 * 		)
	 *
	 */
}
