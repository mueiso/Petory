package com.study.petory.domain.place.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.study.petory.common.exception.CustomException;
import com.study.petory.common.exception.enums.ErrorCode;
import com.study.petory.domain.place.dto.request.PlaceCreateRequestDto;
import com.study.petory.domain.place.dto.request.PlaceStatusChangeRequestDto;
import com.study.petory.domain.place.dto.request.PlaceUpdateRequestDto;
import com.study.petory.domain.place.dto.response.PlaceCreateResponseDto;
import com.study.petory.domain.place.dto.response.PlaceGetAllResponseDto;
import com.study.petory.domain.place.dto.response.PlaceGetResponseDto;
import com.study.petory.domain.place.dto.response.PlaceReviewGetResponseDto;
import com.study.petory.domain.place.dto.response.PlaceUpdateResponseDto;
import com.study.petory.domain.place.entity.Place;
import com.study.petory.domain.place.entity.PlaceType;
import com.study.petory.domain.place.repository.PlaceRepository;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlaceServiceImpl implements PlaceService {

	private final PlaceRepository placeRepository;
	private final UserService userService;

	// 장소 등록
	@Override
	@Transactional
	public PlaceCreateResponseDto savePlace(Long userId, PlaceCreateRequestDto requestDto) {

		Optional<Place> findPlace = placeRepository.findByPlaceNameAndAddress(requestDto.getPlaceName(),
			requestDto.getAddress());

		if (findPlace.isPresent()) {
			throw new CustomException(ErrorCode.DUPLICATE_PLACE);
		}

		User user = userService.getUserById(userId);

		Place place = Place.builder()
			.user(user)
			.placeName(requestDto.getPlaceName())
			.placeInfo(requestDto.getPlaceInfo())
			.placeType(requestDto.getPlaceType())
			.address(requestDto.getAddress())
			.latitude(requestDto.getLatitude())
			.longitude(requestDto.getLongitude())
			.build();

		placeRepository.save(place);

		return PlaceCreateResponseDto.from(place);
	}

	// 전체 장소 조회
	@Override
	@Transactional(readOnly = true)
	public Page<PlaceGetAllResponseDto> findAllPlace(String placeName, PlaceType placeType, Pageable pageable) {
		return placeRepository.findAllPlace(placeName, placeType, pageable);
	}

	// 특정 장소 조회
	@Override
	public PlaceGetResponseDto findByPlaceId(Long placeId) {

		Place findPlace = findPlaceWithPlaceReviewByPlaceId(placeId);

		List<PlaceReviewGetResponseDto> placeReviewList = findPlace.getPlaceReviewList().stream()
			.filter(placeReview -> placeReview.getDeletedAt() == null)
			.map(PlaceReviewGetResponseDto::from)
			.collect(Collectors.toList());

		return PlaceGetResponseDto.from(findPlace, placeReviewList);
	}

	// 장소 수정
	@Override
	@Transactional
	public PlaceUpdateResponseDto updatePlace(Long userId, Long placeId, PlaceUpdateRequestDto requestDto) {

		Place findPlace = findPlaceByPlaceId(placeId);

		if (!findPlace.isEqualUser(userId)) {
			throw new CustomException(ErrorCode.ONLY_AUTHOR_CAN_EDIT);
		}

		findPlace.updatePlace(
			requestDto.getPlaceName(),
			requestDto.getPlaceInfo(),
			requestDto.getPlaceType(),
			requestDto.getLatitude(),
			requestDto.getLongitude());

		return PlaceUpdateResponseDto.from(findPlace);
	}

	// 장소 삭제
	@Override
	@Transactional
	public void deletePlace(Long placeId, PlaceStatusChangeRequestDto requestDto) {

		Place findPlace = findPlaceByPlaceId(placeId);

		if (!findPlace.isDeletedAtNull()) {
			throw new CustomException(ErrorCode.ALREADY_DELETED_PLACE);
		}

		findPlace.deactivateEntity();
		findPlace.updateStatus(requestDto.getPlaceStatus());
	}

	// 삭제된 장소 복구
	@Override
	@Transactional
	public void restorePlace(Long placeId, PlaceStatusChangeRequestDto requestDto) {

		Place findPlace = findPlaceByPlaceId(placeId);

		if (findPlace.isDeletedAtNull()) {
			throw new CustomException(ErrorCode.PLACE_NOT_DELETED);
		}

		findPlace.restoreEntity();
		findPlace.updateStatus(requestDto.getPlaceStatus());
		findPlace.updateReportResetAt();
	}

	// 다른 서비스에서 사용가능하게 설정한 메서드
	// throws CustomException
	@Override
	public Place findPlaceByPlaceId(Long placeId) {
		return placeRepository.findById(placeId)
			.orElseThrow(() -> new CustomException(ErrorCode.PLACE_NOT_FOUND));
	}

	// 다른 서비스에서 사용가능하게 설정한 메서드
	// throws CustomException
	@Override
	public Place findPlaceWithPlaceReviewByPlaceId(Long placeId) {
		return placeRepository.findWithReviewListById(placeId)
			.orElseThrow(() -> new CustomException(ErrorCode.PLACE_NOT_FOUND));
	}

}
