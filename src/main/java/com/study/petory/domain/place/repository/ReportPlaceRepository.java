package com.study.petory.domain.place.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.study.petory.domain.place.entity.Place;
import com.study.petory.domain.place.entity.ReportPlace;
import com.study.petory.domain.user.entity.User;

public interface ReportPlaceRepository extends JpaRepository<ReportPlace, Long> {
	boolean existsByUserAndPlace(User user, Place place);

	long countReportPlaceByPlace(Place place);
}
