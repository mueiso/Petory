package com.study.petory.domain.place.repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.petory.domain.place.dto.response.PlaceGetAllResponseDto;
import com.study.petory.domain.place.entity.Place;
import com.study.petory.domain.place.entity.PlaceStatus;
import com.study.petory.domain.place.entity.PlaceType;
import com.study.petory.domain.place.entity.QPlace;
import com.study.petory.domain.place.entity.QPlaceReview;
import com.study.petory.domain.user.entity.QUser;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlaceCustomRepositoryImpl implements PlaceCustomRepository {

	private final JPAQueryFactory jpaQueryFactory;

	private final QPlace qPlace = QPlace.place;

	private final QPlaceReview qPlaceReview = QPlaceReview.placeReview;

	private final QUser qUser = QUser.user;

	@Override
	public Page<PlaceGetAllResponseDto> findAllPlace(String placeName, PlaceType placeType, String address,
		Pageable pageable) {

		BooleanBuilder booleanBuilder = new BooleanBuilder();

		if (placeName != null && !placeName.isBlank()) {
			booleanBuilder.or(qPlace.placeName.containsIgnoreCase(placeName));
		}

		if (address != null && !address.isBlank()) {
			booleanBuilder.or(qPlace.address.containsIgnoreCase(address));
		}

		if (placeType != null) {
			booleanBuilder.and(qPlace.placeType.eq(placeType));
		}

		List<Place> placeList = jpaQueryFactory
			.selectFrom(qPlace)
			.where(booleanBuilder)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long total = jpaQueryFactory
			.select(qPlace.count())
			.from(qPlace)
			.where(booleanBuilder)
			.fetchOne();

		List<PlaceGetAllResponseDto> dtoList = placeList.stream()
			.map(PlaceGetAllResponseDto::from)
			.toList();

		return new PageImpl<>(dtoList, pageable, total == null ? 0 : total);
	}

	@Override
	public Optional<Place> findWithReviewListById(Long id) {
		return Optional.ofNullable(jpaQueryFactory
			.selectFrom(qPlace)
			.leftJoin(qPlace.placeReviewList, qPlaceReview).fetchJoin()
			.leftJoin(qPlaceReview.user, qUser).fetchJoin()
			.where(qPlace.id.eq(id))
			.fetchOne());
	}

	@Override
	public List<PlaceGetAllResponseDto> findPlaceRankOrderByLikeCountDesc() {

		List<Place> placeList = new ArrayList<>();

		for (PlaceType placeType : PlaceType.values()) {
			Place placeRank = jpaQueryFactory
				.selectFrom(qPlace)
				.where(
					qPlace.placeType.eq(placeType),
					qPlace.placeStatus.eq(PlaceStatus.ACTIVE),
					qPlace.likeCount.isNotNull()
				)
				.orderBy(
					qPlace.likeCount.desc(),
					qPlace.ratio.desc().nullsLast(),
					qPlace.id.asc()
				)
				.limit(1)
				.fetchOne();

			if (placeRank != null) {
				placeList.add(placeRank);
			}
		}

		return placeList.stream()
			.map(PlaceGetAllResponseDto::from)
			.toList();
	}

	// 장소 타입별로 좋아요 순서로 랭킹 1등만 가져오는데
	// 장소 타입이 지금 대충 5개 있어요 [카페 좋아요 1등, 숙소 좋아요 1등]
	// 총 5개의 랭킹이 출력되게끔
	@Override
	public List<PlaceGetAllResponseDto> findPlaceRankOrderByLikeCountDescV2() {
		List<Place> placeList = jpaQueryFactory
			.selectFrom(qPlace)
			.where(
				qPlace.placeStatus.eq((PlaceStatus.ACTIVE)),
				qPlace.likeCount.isNotNull()
			)
			.orderBy(
				qPlace.placeType.asc(),
				qPlace.likeCount.desc(),
				qPlace.ratio.desc().nullsLast(),
				qPlace.id.asc()
			)
			.fetch();

		Map<PlaceType, Place> placeTypePlaceMap = new LinkedHashMap<>();
		for (Place place : placeList) {
			PlaceType placeType = place.getPlaceType();
			if (!placeTypePlaceMap.containsKey(placeType)) {
				placeTypePlaceMap.put(placeType, place);
			}
		}

		return placeTypePlaceMap.values().stream()
			.map(PlaceGetAllResponseDto::from)
			.toList();
	}
}
