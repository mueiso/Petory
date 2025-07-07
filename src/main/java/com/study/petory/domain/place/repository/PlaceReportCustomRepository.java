package com.study.petory.domain.place.repository;

import com.study.petory.domain.place.entity.Place;

public interface PlaceReportCustomRepository {

	long countPlaceReportByPlaceAndIsValidAndReportResetAt(Place place);
}
