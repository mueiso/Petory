package com.study.petory.domain.place.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.util.ReflectionTestUtils;

import com.study.petory.domain.place.dto.response.PlaceLikeResponseDto;
import com.study.petory.domain.place.entity.Place;
import com.study.petory.domain.place.entity.PlaceLike;
import com.study.petory.domain.place.repository.PlaceLikeRepository;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.service.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
public class PlaceLikeServiceTest {

	@Mock
	private PlaceLikeRepository placeLikeRepository;

	@Mock
	private PlaceServiceImpl placeServiceImpl;

	@Mock
	private UserServiceImpl userServiceImpl;

	@Mock
	private RedisTemplate<String, Object> redisTemplate;

	@Mock
	private ZSetOperations<String, Object> zSetOperations;

	@InjectMocks
	private PlaceLikeService placeLikeService;

	private User user;
	private Place place;
	private String key;

	@BeforeEach
	void setUp() {
		user = new User();

		ReflectionTestUtils.setField(user, "id", 1L);

		place = new Place();

		ReflectionTestUtils.setField(place, "id", 1L);

		when(placeServiceImpl.findPlaceByPlaceId(1L)).thenReturn(place);

		key = "testKey";

		when(placeServiceImpl.makeKey(place.getPlaceType())).thenReturn(key);

		when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
	}

	@Test
	@DisplayName("장소 좋아요")
	void likePlace() {
		when(placeLikeRepository.existsByPlaceIdAndUserId(1L, 1L)).thenReturn(false);

		PlaceLike placeLike = new PlaceLike(place, user);
		when(placeLikeRepository.save(any(PlaceLike.class))).thenReturn(placeLike);

		when(redisTemplate.opsForZSet().incrementScore(key, 1L, 1)).thenReturn(1D);
		when(redisTemplate.opsForZSet().incrementScore("place:rank:ALL", 1L, 1)).thenReturn(1D);

		PlaceLikeResponseDto responseDto = placeLikeService.likePlace(1L, 1L);

		assertAll("장소 좋아요 로직 검증",
			() -> assertEquals(1L, responseDto.getPlaceId()),
			() -> assertEquals(1L, responseDto.getUserId()),
			() -> assertEquals(1L, responseDto.getLikeCount()),
			() -> assertTrue(responseDto.isLiked())
		);
	}

	@Test
	@DisplayName("장소 싫어요")
	void disLikePlace() {
		when(placeLikeRepository.existsByPlaceIdAndUserId(1L, 1L)).thenReturn(true);

		when(redisTemplate.opsForZSet().incrementScore(key, 1L, -1)).thenReturn(0D);
		when(redisTemplate.opsForZSet().incrementScore("place:rank:ALL", 1L, -1)).thenReturn(0D);

		PlaceLikeResponseDto responseDto = placeLikeService.likePlace(1L, 1L);

		assertAll("장소 좋아요 로직 검증",
			() -> assertEquals(1L, responseDto.getPlaceId()),
			() -> assertEquals(1L, responseDto.getUserId()),
			() -> assertEquals(-1, responseDto.getLikeCount()),
			() -> assertFalse(responseDto.isLiked())
		);
	}

}
