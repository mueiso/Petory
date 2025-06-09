package com.study.petory.domain.place.entity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import com.study.petory.common.entity.TimeFeatureBasedEntity;
import com.study.petory.domain.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "tb_place")
@NoArgsConstructor
public class Place extends TimeFeatureBasedEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(nullable = false, length = 30)
	private String placeName;

	@OneToMany(mappedBy = "place")
	private List<PlaceReview> placeReviewList;

	@Column(nullable = false)
	private String placeInfo;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private PlaceType placeType; // type -> placeType으로 수정

	@Column(precision = 2, scale = 1)        // 추후에 NOTNULL로 수정 예정, precision : 전체 자리 수, scale : 그 중 소수점 자리 수
	private BigDecimal ratio;

	// 전체 주소
	@Column(nullable = false, length = 100)
	private String address;

	// 위도
	@Column(nullable = false, precision = 9, scale = 6)
	private BigDecimal latitude;

	// 경도
	@Column(nullable = false, precision = 10, scale = 6)
	private BigDecimal longitude;

	// status 기본 상태 = 영업중
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 10)
	private PlaceStatus placeStatus = PlaceStatus.ACTIVE;

	@Builder
	public Place(User user, String placeName, String placeInfo, PlaceType placeType, BigDecimal ratio, String address,
		BigDecimal latitude, BigDecimal longitude) {
		this.user = user;
		this.placeName = placeName;
		this.placeInfo = placeInfo;
		this.placeType = placeType;
		this.ratio = ratio;
		this.address = address;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	// soft delete 구현을 위한 메서드
	public void updateStatus(PlaceStatus placeStatus) {
		this.placeStatus = placeStatus;
	}

	// PlaceUpdateRequestDto null 가능 여부에 따른 update 메서드
	public void updatePlace(String placeName, String placeInfo, PlaceType placeType, BigDecimal latitude,
		BigDecimal longitude) {
		if (placeName != null) {
			this.placeName = placeName;
		}

		if (placeInfo != null) {
			this.placeInfo = placeInfo;
		}

		if (placeType != null) {
			this.placeType = placeType;
		}

		if (latitude != null) {
			this.latitude = latitude;
		}

		if (longitude != null) {
			this.longitude = longitude;
		}
	}

	// 평균 평점 계산 로직
	public void updateRatio(List<PlaceReview> placeReviewList) {
		Integer sumRatio = 0;
		int countPlaceReview = 0;

		for (PlaceReview placeReview : placeReviewList) {
			if (placeReview.getDeletedAt() == null) {
				sumRatio += placeReview.getRatio();
				countPlaceReview++;
			}
		}

		// 만약 리뷰 갯수가 0개인 경우 0으로 반환
		if (countPlaceReview == 0) {
			this.ratio = BigDecimal.ZERO;
			return;
		}

		// 정수형 타입을 BigDecimal 형태로 변환함과 동시에 나누는 로직. 소수점 첫째 자리까지 계산, 둘째 자리에서 반올림.
		this.ratio = BigDecimal.valueOf(sumRatio)
			.divide(BigDecimal.valueOf(countPlaceReview), 1, RoundingMode.HALF_UP);
	}
}
