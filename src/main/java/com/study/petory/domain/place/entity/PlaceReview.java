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
@Table(name = "tb_place_review")
@NoArgsConstructor
public class PlaceReview extends TimeFeatureBasedEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "place_id", nullable = false)
	private Place place;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(nullable = false)
	private String content;

	@Column(nullable = false)
	private Integer ratio;

	@Builder
	public PlaceReview(Place place, User user, String content, Integer ratio) {
		this.place = place;
		this.user = user;
		this.content = content;
		this.ratio = ratio;
	}

	// PlaceReviewUpdateRequestDto null 가능 여부에 따른 update 메서드
	public void updatePlaceReview(String content, Integer ratio) {
		if (content != null) {
			this.content = content;
		}

		if (ratio != null) {
			this.ratio = ratio;
		}
	}
}
