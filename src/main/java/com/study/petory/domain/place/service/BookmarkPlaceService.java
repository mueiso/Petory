package com.study.petory.domain.place.service;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.petory.common.exception.CustomException;
import com.study.petory.common.exception.enums.ErrorCode;
import com.study.petory.domain.place.dto.request.BookmarkPlaceRequestDto;
import com.study.petory.domain.place.entity.Place;
import com.study.petory.domain.place.entity.PlaceType;
import com.study.petory.domain.place.repository.PlaceRepository;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookmarkPlaceService {

	private final PlaceRepository placeRepository;
	private final ObjectMapper objectMapper;
	private final UserRepository userRepository;

	public void writeJsonData(String filePath) {

		// User는 추후에 수정 예정
		User user = userRepository.findById(1L).orElseThrow();

		// filePath에 해당하는 폴더에서 fixed.json으로 끝나는 파일만 가져오는 과정
		File folder = new File(filePath);
		File[] fileArrays = folder.listFiles((dir, name) -> name.endsWith("-fixed.json"));

		for (File file : fileArrays) {
			try {
				// Json 배열 파일을 Dto로 변환
				// objectMapper를 이용하여 배열로 변환한 부분을 리스트로 바꾸는 과정
				List<BookmarkPlaceRequestDto> dtoList = Arrays.asList(
					// readValue는 Json파일을 dto로 변환하는 것
					objectMapper.readValue(file, BookmarkPlaceRequestDto[].class)
				);

				// Dto를 Place Entity로 변환
				List<Place> placeList = dtoList.stream()
					.map(dto -> convertToPlace(dto, user))
					.collect(Collectors.toList());

				placeRepository.saveAll(placeList);
			} catch (Exception e) {
				throw new CustomException(ErrorCode.JSON_PARSE_ERROR);
			}
		}
	}

	// Dto를 Place Entity로 변환
	private Place convertToPlace(BookmarkPlaceRequestDto dto, User user) {
		return Place.builder()
			.user(user)
			.placeName(dto.getPlaceName())
			.address(dto.getAddress())
			.latitude(dto.getLatitude())
			.longitude(dto.getLongitude())
			.placeType(convertToEnum(dto.getPlaceType()))
			.placeInfo(null)
			.ratio(null)
			.build();
	}

	// Json에서의 placeType인 mcidName을 PlaceType Enum에 맞게 변환
	private PlaceType convertToEnum(String mcidName) {
		return switch (mcidName) {
			case "카페" -> PlaceType.CAFE;
			case "숙박" -> PlaceType.ACCOMMODATION;
			case "음식점" -> PlaceType.RESTAURANT;
			default -> PlaceType.ETC;
		};
	}
}
