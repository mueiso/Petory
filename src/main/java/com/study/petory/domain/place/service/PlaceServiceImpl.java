package com.study.petory.domain.place.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
import com.study.petory.domain.place.entity.PlaceImage;
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
	private final PlaceImageService placeImageService;
	private final RedisTemplate<String, Object> redisTemplate;

	// 장소 등록
	@Override
	@Transactional
	public PlaceCreateResponseDto savePlace(Long userId, PlaceCreateRequestDto requestDto, List<MultipartFile> images) {

		Optional<Place> findPlace = placeRepository.findByPlaceNameAndAddress(requestDto.getPlaceName(),
			requestDto.getAddress());

		if (findPlace.isPresent()) {
			throw new CustomException(ErrorCode.DUPLICATE_PLACE);
		}

		User user = userService.findUserById(userId);

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

		List<String> urls = new ArrayList<>();

		if (images != null && !images.isEmpty()) {
			urls = placeImageService.uploadAndSaveAll(images, place);
		}

		return PlaceCreateResponseDto.of(place, urls);
	}

	// 전체 장소 조회
	@Override
	@Transactional(readOnly = true)
	public Page<PlaceGetAllResponseDto> findAllPlace(String placeName, PlaceType placeType, String address,
		Pageable pageable) {
		return placeRepository.findAllPlace(placeName, placeType, address, pageable);
	}

	// 특정 장소 조회
	@Override
	public PlaceGetResponseDto findByPlaceId(Long placeId) {

		Place findPlace = findPlaceWithPlaceReviewByPlaceId(placeId);

		List<PlaceReviewGetResponseDto> placeReviewList = findPlace.getPlaceReviewList().stream()
			.filter(placeReview -> placeReview.getDeletedAt() == null)
			.map(PlaceReviewGetResponseDto::from)
			.toList();

		List<PlaceImage> images = placeImageService.findImagesByPlaceId(placeId);

		return PlaceGetResponseDto.of(
			findPlace,
			images.stream()
				.map(PlaceImage::getUrl)
				.toList(),
			placeReviewList);
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

	// 장소 사진 추가
	@Override
	@Transactional
	public void addImages(Long userId, Long placeId, List<MultipartFile> images) {

		Place findPlace = findPlaceByPlaceId(placeId);

		List<PlaceImage> placeImages = placeImageService.uploadAndReturnEntities(images, findPlace);
		for (PlaceImage image : placeImages) {
			findPlace.addImage(image);
		}
	}

	// 장소 사진 삭제
	@Override
	@Transactional
	public void deleteImage(Long userId, Long placeId, Long imageId) {
		Place findPlace = findPlaceByPlaceId(placeId);

		PlaceImage image = placeImageService.findImageById(imageId);

		if (!findPlace.isEqualId(image.getPlace().getId())) {
			throw new CustomException(ErrorCode.INVALID_INPUT);
		}

		placeImageService.deleteImageInternal(image);
		findPlace.getImages().remove(image);
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
		return placeRepository.findWithReviewListByPlaceId(placeId)
			.orElseThrow(() -> new CustomException(ErrorCode.PLACE_NOT_FOUND));
	}

	// 인기 랭킹 조회
	@Override
	public List<PlaceGetAllResponseDto> findPlaceRank(PlaceType placeType) {
		String key = makeKey(placeType);

		// Top 10 조회
		Set<Object> placeIdSet = redisTemplate.opsForZSet().reverseRange(key, 0, 9);

		// Ranking 데이터에 이상이 있는 게 아니라 그저 아직 랭킹이 형성되지 않았기 때문에 예외 대신 빈 List를 반환
		if (placeIdSet == null || placeIdSet.isEmpty()) {
			return List.of();
		}

		List<Long> placeIdList = placeIdSet.stream()
			.map(object -> Long.parseLong(object.toString()))
			.toList();

		List<Place> placeList = placeRepository.findAllById(placeIdList);

		// 여기서 문제가 발생했던것!!!!!
		return placeList.stream()
			.map(place -> {
				List<String> imageUrls = place.getImages().stream()
					.map(PlaceImage::getUrl)
					.toList();
				return PlaceGetAllResponseDto.of(place, imageUrls);
			})
			.toList();
	}

	// Redis key 생성 로직. (예시 - "place:rank:{PlaceType}"
	@Override
	public String makeKey(PlaceType placeType) {
		StringBuilder stringBuilder = new StringBuilder("place:rank");

		if (placeType != null) {
			stringBuilder.append(":").append(placeType);
		} else {
			stringBuilder.append(":ALL");
		}

		return stringBuilder.toString();
	}

	// String -> PlaceType 변환 로직
	@Override
	public PlaceType parsePlaceType(String placeType) {
		if (placeType == null || "ALL".equalsIgnoreCase(placeType)) {
			return null;
		}
		try {
			return PlaceType.valueOf(placeType.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new CustomException(ErrorCode.INVALID_PARAMETER);
		}
	}
}