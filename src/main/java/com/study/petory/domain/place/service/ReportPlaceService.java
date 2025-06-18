package com.study.petory.domain.place.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.study.petory.common.exception.CustomException;
import com.study.petory.common.exception.enums.ErrorCode;
import com.study.petory.domain.place.dto.request.ReportPlaceRequestDto;
import com.study.petory.domain.place.entity.Place;
import com.study.petory.domain.place.entity.PlaceStatus;
import com.study.petory.domain.place.entity.ReportPlace;
import com.study.petory.domain.place.repository.ReportPlaceRepository;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportPlaceService {

	private final ReportPlaceRepository reportPlaceRepository;
	private final PlaceService placeService;
	private final UserService userService;

	@Transactional
	public void reportPlace(Long userId, Long placeId, ReportPlaceRequestDto requestDto) {

		Place findPlace = placeService.findPlaceByPlaceId(placeId);

		if (!findPlace.isDeletedAtNull()) {
			throw new CustomException(ErrorCode.ALREADY_INACTIVE_PLACE);
		}

		User findUser = userService.getUserById(userId);

		if (reportPlaceRepository.existsByUserAndPlace(findUser, findPlace)) {
			throw new CustomException(ErrorCode.ALREADY_REPORT_PLACE);
		}

		ReportPlace reportPlace = ReportPlace.builder()
			.user(findUser)
			.place(findPlace)
			.content(requestDto.getContent())
			.build();

		reportPlaceRepository.save(reportPlace);

		if (reportPlaceRepository.countReportPlaceByPlace(findPlace) >= 10) {
			findPlace.deactivateEntity();
			findPlace.updateStatus(PlaceStatus.INACTIVE);
		}
	}
}
