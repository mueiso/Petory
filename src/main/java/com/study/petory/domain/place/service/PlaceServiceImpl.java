package com.study.petory.domain.place.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.study.petory.domain.place.dto.request.PlaceCreateRequestDto;
import com.study.petory.domain.place.dto.response.PlaceCreateResponseDto;
import com.study.petory.domain.place.entity.Place;
import com.study.petory.domain.place.repository.PlaceRepository;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlaceServiceImpl implements PlaceService {

	private final PlaceRepository placeRepository;
	private final UserRepository userRepository;

	// 장소 등록
	@Override
	@Transactional
	public PlaceCreateResponseDto savePlace(PlaceCreateRequestDto requestDto) {

		User user = userRepository.findById(1L).orElseThrow();

		Place place = Place.builder()
			.user(user)
			.placeName(requestDto.getPlaceName())
			.placeInfo(requestDto.getPlaceInfo())
			.placeType(requestDto.getPlaceType())
			// .ratio()
			.address(requestDto.getAddress())
			.latitude(requestDto.getLatitude())
			.longitude(requestDto.getLongitude())
			.build();

		placeRepository.save(place);

		return PlaceCreateResponseDto.fromPlace(place);
	}

}
