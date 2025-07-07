package com.study.petory.domain.place.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.study.petory.domain.place.entity.PlaceReport;
import com.study.petory.domain.user.entity.User;

public interface PlaceReportRepository extends JpaRepository<PlaceReport, Long>, PlaceReportCustomRepository {

	boolean existsByUserIdAndPlaceId(Long userId, Long placeId);

	List<PlaceReport> findByUser(User user);
}
