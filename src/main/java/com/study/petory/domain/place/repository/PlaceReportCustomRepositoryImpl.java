package com.study.petory.domain.place.repository;

import java.util.Optional;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.petory.domain.place.entity.Place;
import com.study.petory.domain.place.entity.QPlaceReport;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlaceReportCustomRepositoryImpl implements PlaceReportCustomRepository{

	private final JPAQueryFactory jpaQueryFactory;

	private final QPlaceReport qPlaceReport = QPlaceReport.placeReport;

	@Override
	public long countPlaceReportByPlaceAndIsValidAndReportResetAt(Place place) {
		return Optional.ofNullable(jpaQueryFactory
			.select(qPlaceReport.count())
			.from(qPlaceReport)
			.where(
				qPlaceReport.place.eq(place),
				qPlaceReport.isValid.eq(true),
				place.getReportResetAt() == null ? null : qPlaceReport.createdAt.after(place.getReportResetAt())
			)
			.fetchOne())
			.orElse(0L);
	}
}
