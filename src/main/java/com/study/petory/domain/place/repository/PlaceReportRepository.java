package com.study.petory.domain.place.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.study.petory.domain.place.entity.PlaceReport;

public interface PlaceReportRepository extends JpaRepository<PlaceReport, Long>, PlaceReportCustomRepository {

	boolean existsByUserIdAndPlaceId(Long userId, Long placeId);
}
