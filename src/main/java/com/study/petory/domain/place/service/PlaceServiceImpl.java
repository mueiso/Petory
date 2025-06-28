package com.study.petory.domain.place.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
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
	private final RedisTemplate<String, Object> redisTemplate;

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

	// @Cacheable(value = "placeRankRedisCache", key = "'rank'")
	// @Override
	// public List<PlaceGetAllResponseDto> findPlaceRank() {
	// 	return placeRepository.findPlaceRankOrderByLikeCountDesc();
	// }
	//
	// // 리스트 말고 객체 하나로 가져와라
	// // 레디스 역직렬화 검색하면 나올거임
	// @Scheduled(cron = "*/10 * * * * *")
	// @Transactional(readOnly = true)
	// @Override
	// public void findPlaceRankSchedule() {
	// 	System.out.println("✅✅✅✅  인기 랭킹 메서드 실행V1  ✅✅✅✅");
	// 	List<PlaceGetAllResponseDto> placeRankDtoList = placeRepository.findPlaceRankOrderByLikeCountDesc();
	// 	redisTemplate.opsForValue().set("placeRankRedisCache::rank", placeRankDtoList, remainderTime());
	//
	// 	// Object cacheObj = redisTemplate.opsForValue().get("placeRankRedisCache::rank");
	// 	// if (cacheObj instanceof List<?>) {
	// 	// 	List<?> list = (List<?>)cacheObj;
	// 	// 	List<PlaceGetAllResponseDto> dtoList = list.stream()
	// 	// 		.map(o -> {
	// 	// 			if (o instanceof PlaceGetAllResponseDto dto) {
	// 	// 				return dto;
	// 	// 			}
	// 	// 			// LinkedHashMap 인 경우 수동으로 매핑
	// 	// 			else if (o instanceof LinkedHashMap<?, ?> map) {
	// 	// 				ObjectMapper objectMapper = new ObjectMapper();
	// 	// 				return objectMapper.convertValue(map, PlaceGetAllResponseDto.class);
	// 	// 			}
	// 	// 			return null;
	// 	// 		})
	// 	// 		.filter(Objects::nonNull)
	// 	// 		.collect(Collectors.toList());
	// 	// 	for (PlaceGetAllResponseDto dto : dtoList) {
	// 	// 		System.out.println("placeId : " + dto.getId());
	// 	// 		System.out.println("placeType : " + dto.getPlaceType());
	// 	// 		System.out.println("likeCount : " + dto.getLikeCount());
	// 	// 	}
	// 	// }
	// }

	@Scheduled(cron = "0 * * * * *")
	@Transactional(readOnly = true)
	@Override
	public void findPlaceRankByZSet() {
		System.out.println("💥💥💥💥  인기 랭킹 메서드 실행V2  💥💥💥💥");
		List<PlaceGetAllResponseDto> placeRankDtoList = placeRepository.findPlaceRankOrderByLikeCountDescV2();
		for (PlaceGetAllResponseDto dto : placeRankDtoList) {
			PlaceType placeType = dto.getPlaceType();
			String redisKey = "placeRank::" + placeType.getDisplayName();
			Long placeId = dto.getId();
			Long likeCount = dto.getLikeCount();

			// 인기 장소 랭킹 -> 사용자가 map.html 접속 -> 자동으로 인기 장소(좋아요 수 기반)가 출력되게
			// 여기서 나오는 인기장소가 placeType 별로 상위 1개씩 출력되게 하려고 했던것...
			// 검색을 했을 때 검색어 자체가 redis에 저장이 된다
			// 레디스에 검색어가 없으면 새로 저장이 되고 있으면 숫자가 오른다
			// 장소 -> '서울' address contains '서울' List GetMapping
			// redis에 서울이 저장됨
			// 서울 -> score가 올라간다
			// 장소 -> 서울 서울장소
			// 서울 -> 스타벅스 서울역점 좋아요 100개
			// 서울 -> 스타벅스 명동점 좋아요 0개
			// ZSet에 placeId와 likeCount를 score로 저장
			redisTemplate.opsForZSet().add(redisKey, placeId, likeCount);
			System.out.println("redisKey : " + redisKey);
			System.out.println("placeId : " + placeId);
			System.out.println("likeCount : " + likeCount);
		}
	}

	// 지역에 대한 검색을 넣어놓고
	// 	강남에 대한 랭킹이 redis에 들어간다?
	// ZSet increment 하는 방법 찾아보기. 해당 키에서 평균 range를 스코어링?
	// ZSet increase score
	// DB 들릴 필요가없다.................................................................................................

	// lettuce랑 redis 공부해보기
}