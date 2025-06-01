package com.study.petory.domain.place.service;

import org.springframework.stereotype.Service;

import com.study.petory.common.util.EntityFetcher;
import com.study.petory.domain.place.repository.PlaceReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlaceReviewServiceImpl implements PlaceReviewService{

	private final PlaceReviewRepository placeReviewRepository;
	private final EntityFetcher entityFetcher;
}
