package com.study.petory.domain.place.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.study.petory.domain.place.entity.Place;
import com.study.petory.domain.place.entity.PlaceReport;
import com.study.petory.domain.user.entity.User;

public interface PlaceReportRepository extends JpaRepository<PlaceReport, Long> {
	boolean existsByUserAndPlace(User user, Place place);

	long countPlaceReportByPlaceAndValid(Place place, boolean valid);
}
