package com.study.petory.domain.calendar.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.DynamicUpdate;

import com.study.petory.common.entity.UpdateBasedEntity;
import com.study.petory.domain.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@DynamicUpdate
@Table(
	name = "tb_event",
	indexes = {
		@Index(name = "index_user_id_start", columnList = "user_id, start_date")
	}
)
@NoArgsConstructor
public class Event extends UpdateBasedEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column
	private String googleEventId;

	@Column(length = 100)
	private String title;

	@Column(nullable = false)
	private LocalDateTime startDate;

	@Column
	private LocalDateTime endDate;

	@Column
	private String timeZone;

	// 기본 true
	@Column
	private Boolean isAllDay;

	@Column
	private String rrule;

	@Column
	private String recurrenceEnd;

	@Column
	private String rDate;

	@Column
	private String exDate;

	@Column(length = 300)
	private String description;

	// 프론트에서 null 이라면 기본 색상
	@Column
	private String color;

	/*
	장소 재호님 도움이 필요할 수도 있음
	구글 캘린더에 저장되는 장소는 구글 맵
	해당 주소를 카카오 맵에서 사용하기에는 주소가 같은 형식이 아님
	구글맵에는 있어도 카카오 맵에는 없을 수도 있음
	구글맵에서 가져온 위도 경도로 카카오 맵 검색이 가능한 것인가? (1. 위경도로 검색 2. 추가 정보로 추가 검색)
	@Column
	private String Location; 도로명 주소
	private Long placeId; null이 아니라면 place id로 검색
	*/

	// 알람 채원님 도움을 받아야 함
	// @Column
	// private String Alarm;

	@Builder
	public Event(User user, String title, LocalDateTime startDate, LocalDateTime endDate, String timeZone, boolean isAllDay, String rrule, String recurrenceEnd,
		String rDate, String exDate, String description, String color) {
		this.user = user;
		this.title = title;
		this.startDate = startDate;
		this.endDate = endDate;
		this.timeZone = timeZone;
		this.isAllDay = isAllDay;
		this.rrule = rrule;
		this.recurrenceEnd = recurrenceEnd;
		this.rDate = rDate;
		this.exDate = exDate;
		this.description = description;
		this.color = color;
	}

	public void updateEvent(String title, LocalDateTime start, LocalDateTime end, String timeZone, boolean isAllDay, String rrule, String recurrenceEnd,
		String rDate, String exDate, String description, String color) {
		this.title = title;
		this.startDate = start;
		this.endDate = end;
		this.timeZone = timeZone;
		this.isAllDay = isAllDay;
		this.rrule = rrule;
		this.recurrenceEnd = recurrenceEnd;
		this.rDate = rDate;
		this.exDate = exDate;
		this.description = description;
		this.color = color;
	}

	public boolean isEqualUser(Long userId) {
		return this.user.isEqualId(userId);
	}
}
