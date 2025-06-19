package com.study.petory.domain.place.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.study.petory.common.exception.CustomException;
import com.study.petory.common.exception.enums.ErrorCode;
import com.study.petory.domain.place.dto.request.PlaceReportCancelRequestDto;
import com.study.petory.domain.place.dto.request.PlaceReportRequestDto;
import com.study.petory.domain.place.entity.Place;
import com.study.petory.domain.place.entity.PlaceStatus;
import com.study.petory.domain.place.entity.PlaceReport;
import com.study.petory.domain.place.repository.PlaceReportRepository;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlaceReportService {

	private final PlaceReportRepository placeReportRepository;
	private final PlaceService placeService;
	private final UserService userService;

	@Transactional
	public void reportPlace(Long userId, Long placeId, PlaceReportRequestDto requestDto) {

		Place findPlace = placeService.findPlaceByPlaceId(placeId);

		if (!findPlace.isDeletedAtNull()) {
			throw new CustomException(ErrorCode.ALREADY_INACTIVE_PLACE);
		}

		User findUser = userService.getUserById(userId);

		if (placeReportRepository.existsByUserAndPlace(findUser, findPlace)) {
			throw new CustomException(ErrorCode.ALREADY_REPORT_PLACE);
		}

		PlaceReport placeReport = PlaceReport.builder()
			.user(findUser)
			.place(findPlace)
			.content(requestDto.getContent())
			.build();

		placeReportRepository.save(placeReport);

		if (placeReportRepository.countPlaceReportByPlaceAndValid(findPlace, true) >= 10) {
			findPlace.deactivateEntity();
			findPlace.updateStatus(PlaceStatus.INACTIVE);
		}
	}

	@Transactional
	public void cancelReportPlace(Long userId, Long placeId, Long reportId, PlaceReportCancelRequestDto requestDto) {

		PlaceReport findPlaceReport = findPlaceReportByPlaceReportId(reportId);

		if(!findPlaceReport.isEqualPlace(placeId)) {
			throw new CustomException(ErrorCode.INCONSISTENT_PLACE);
		}

		if(!findPlaceReport.isValid()) {
			throw new CustomException(ErrorCode.ALREADY_INVALID_REPORT);
		}

		findPlaceReport.updatePlaceReport(userId, requestDto.getReason());
		findPlaceReport.deactivateEntity();
	}

	public PlaceReport findPlaceReportByPlaceReportId(Long reportId) {
		return placeReportRepository.findById(reportId)
			.orElseThrow(() -> new CustomException(ErrorCode.PLACE_REPORT_NOT_FOUND));
	}
}
