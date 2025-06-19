package com.study.petory.domain.place.repository;

import java.util.Optional;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.petory.domain.place.entity.Place;
import com.study.petory.domain.place.entity.QPlaceReport;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlaceReportCustomRepositoryImpl implements PlaceReportCustomRepository {

	private final JPAQueryFactory jpaQueryFactory;

	private final QPlaceReport qPlaceReport = QPlaceReport.placeReport;

	@Override
	public long countPlaceReportByPlaceAndIsValidAndReportResetAt(Place place) {

		BooleanBuilder booleanBuilder = new BooleanBuilder();
		booleanBuilder.and(qPlaceReport.place.id.eq(place.getId()));
		booleanBuilder.and(qPlaceReport.isValid.eq(true));

		if (place.getReportResetAt() != null) {
			booleanBuilder.and(qPlaceReport.createdAt.after(place.getReportResetAt()));
		}

		return Optional.ofNullable(jpaQueryFactory
				.select(qPlaceReport.count())
				.from(qPlaceReport)
				.where(booleanBuilder)
				.fetchOne())
			.orElse(0L);
	}
}
