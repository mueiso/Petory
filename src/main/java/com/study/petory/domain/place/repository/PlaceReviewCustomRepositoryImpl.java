package com.study.petory.domain.place.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.petory.domain.place.entity.QPlaceReview;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlaceReviewCustomRepositoryImpl implements PlaceReviewCustomRepository {

	private final JPAQueryFactory jpaQueryFactory;

	private final QPlaceReview qPlaceReview = QPlaceReview.placeReview;

	@Override
	public Double calculateAvgRatioByPlaceId(Long placeId) {
		return jpaQueryFactory
			.select(qPlaceReview.ratio.avg())
			.from(qPlaceReview)
			.where(qPlaceReview.place.id.eq(placeId)
				, (qPlaceReview.deletedAt.isNull()))
			.fetchOne();
	}
}
