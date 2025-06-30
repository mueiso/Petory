package com.study.petory.domain.place.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.study.petory.common.exception.CustomException;
import com.study.petory.common.exception.enums.ErrorCode;
import com.study.petory.domain.place.dto.response.PlaceLikeResponseDto;
import com.study.petory.domain.place.entity.Place;
import com.study.petory.domain.place.entity.PlaceLike;
import com.study.petory.domain.place.repository.PlaceLikeRepository;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlaceLikeService {

	private final PlaceLikeRepository placeLikeRepository;
	private final PlaceService placeService;
	private final UserService userService;
	private final RedisTemplate<String, Object> redisTemplate;

	@Transactional
	public PlaceLikeResponseDto likePlace(Long userId, Long placeId) {
		// 삭제된 유저가 좋아요 시도할 경우 NPE 방지 (검증된 유저)
		User user;
		try {
			user = userService.findUserById(userId);
		} catch (CustomException e) {
			throw new CustomException(ErrorCode.USER_NOT_FOUND);
		}

		Place findPlace = placeService.findPlaceByPlaceId(placeId);
		String key = placeService.makeKey(findPlace.getPlaceType());

		boolean isLiked = placeLikeRepository.existsByPlace_IdAndUser_Id(placeId, userId);

		if (isLiked) {
			placeLikeRepository.deleteByPlace_IdAndUser_Id(placeId, userId);
			findPlace.decreaseLikeCount();
			isLiked = false;

			redisTemplate.opsForZSet().incrementScore(key, placeId, -1);
			redisTemplate.opsForZSet().incrementScore("place:rank:ALL", placeId, -1);

		} else {
			PlaceLike placeLike = new PlaceLike(findPlace, user);
			findPlace.increaseLikeCount();
			placeLikeRepository.save(placeLike);
			isLiked = true;

			redisTemplate.opsForZSet().incrementScore(key, placeId, 1);
			redisTemplate.opsForZSet().incrementScore("place:rank:ALL", placeId, 1);
		}

		return PlaceLikeResponseDto.builder()
			.placeId(placeId)
			.userId(userId)
			.likeCount(findPlace.getLikeCount())
			.isLiked(isLiked)
			.build();
	}
}
