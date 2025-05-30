package com.study.petory.domain.place.entity;

import java.math.BigDecimal;

import com.study.petory.common.entity.BaseEntityWithBothAt;
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
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "place")
@NoArgsConstructor
public class Place extends BaseEntityWithBothAt {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(nullable = false, length = 30)
	private String placeName;

	@Column(nullable = false)
	private String placeInfo;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private PlaceType placeType; // type -> placeType으로 수정

	@Column(nullable = false, precision = 2, scale = 1)		// precision : 전체 자리 수, scale : 그 중 소수점 자리 수
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
	private Status status = Status.ACTIVE;

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

	// soft delete, 영업 중지로 status 변환
	public void deactivatePlace() {
		this.status = Status.INACTIVE;
	}

	// soft delete, 폐업으로 status 변환 -> 잘못 신고가 들어와서 폐업으로 오인할 수도 있어서 일단 소프트 딜리트 하고 일정 기간 이후에 하드 딜리트 되도록!
	public void deletePlace() {
		this.status = Status.DELETED;
	}
}
