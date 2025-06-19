package com.study.petory.domain.place.entity;

import com.study.petory.common.entity.TimeFeatureBasedEntity;
import com.study.petory.domain.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

@Entity
@Getter
@Table(name = "tb_place_report")
@NoArgsConstructor
public class PlaceReport extends TimeFeatureBasedEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "place_id", nullable = false)
	private Place place;

	@Column(nullable = false)
	private String content;

	@Column
	private boolean isValid = true;

	@Column
	private Long adminId;

	@Column(length = 300)
	private String reason;

	@Builder
	public PlaceReport(User user, Place place, String content) {
		this.user = user;
		this.place = place;
		this.content = content;
	}

	// 관리자 판단으로 신고 취소하는 메서드
	public void updatePlaceReport(Long adminId, String reason) {
		this.isValid = false;
		this.adminId = adminId;
		this.reason = reason;
	}

	public boolean isEqualUser(Long userId) {
		return this.user.isEqualId(userId);
	}

	public boolean isEqualPlace(Long placeId) {
		return this.place.isEqualId(placeId);
	}
}
