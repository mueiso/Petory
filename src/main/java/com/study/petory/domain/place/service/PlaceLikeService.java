package com.study.petory.domain.place.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.study.petory.domain.place.dto.response.PlaceLikeResponseDto;
import com.study.petory.domain.place.entity.Place;
import com.study.petory.domain.place.entity.PlaceLike;
import com.study.petory.domain.place.repository.PlaceLikeRepository;
import com.study.petory.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlaceLikeService {

	private final PlaceLikeRepository placeLikeRepository;
	private final PlaceService placeService;
	private final UserService userService;

	@Transactional
	public PlaceLikeResponseDto likePlace(Long userId, Long placeId) {

		Place findPlace = placeService.findPlaceByPlaceId(placeId);

		boolean isLiked = placeLikeRepository.existsByPlace_IdAndUser_Id(placeId, userId);

		if (isLiked) {
			placeLikeRepository.deleteByPlace_IdAndUser_Id(placeId, userId);
			findPlace.decreaseLikeCount();
			isLiked = false;
		} else {
			PlaceLike placeLike = new PlaceLike(findPlace, userService.findUserById(userId));
			findPlace.increaseLikeCount();
			placeLikeRepository.save(placeLike);
			isLiked = true;
		}

		return PlaceLikeResponseDto.builder()
			.placeId(placeId)
			.userId(userId)
			.likeCount(findPlace.getLikeCount())
			.isLiked(isLiked)
			.build();
	}
}
