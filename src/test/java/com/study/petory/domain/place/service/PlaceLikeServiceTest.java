package com.study.petory.domain.place.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.study.petory.domain.place.repository.PlaceLikeRepository;
import com.study.petory.domain.user.service.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
public class PlaceLikeServiceTest {

	private PlaceLikeRepository placeLikeRepository;

	private PlaceServiceImpl placeServiceImpl;

	private UserServiceImpl userServiceImpl;

}
