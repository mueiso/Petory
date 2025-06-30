package com.study.petory.domain.calendar.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
		// 이대로 사용해도 괜찮은 것인가?
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

	@Column(length = 100)
	private String title;

	@Column(nullable = false)
	private LocalDateTime startDate;

	@Column
	private LocalDateTime endDate;

	@Column(nullable = false)
	private String timeZone;

	// 기본 true
	@Column
	private Boolean isAllDay;

	@Column
	private String rrule;

	@Column
	private LocalDateTime recurrenceEnd;

	@Column
	private String rDate;

	@Column
	private String exDate;

	@Column(length = 300)
	private String description;

	// 프론트에서 null 이라면 기본 색상
	@Column
	private String color;

	@Builder
	public Event(User user, String title, LocalDateTime startDate, LocalDateTime endDate, String timeZone, boolean isAllDay, String rrule, LocalDateTime recurrenceEnd,
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

	public void updateEvent(String title, LocalDateTime start, LocalDateTime end, String timeZone, boolean isAllDay, String rrule, LocalDateTime recurrenceEnd,
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

	public boolean isRruleBlank() {
		return this.rrule == null || this.rrule.isEmpty();
	}

	public boolean isRDateBlank() {
		return this.rDate == null || this.rDate.isEmpty();
	}

	public boolean isExDateBlank() {
		return this.exDate == null || this.exDate.isEmpty();
	}

	public List<String> getRecurrence() {
		List<String> responseList = new ArrayList<>();
		if (!isRruleBlank()) {
			responseList.add("RRULE:" + this.rrule);
		}
		if (!isRDateBlank()) {
			responseList.add("RDATE:" + this.rDate);
		}
		if (!isExDateBlank()) {
			responseList.add("EXDATE:" + this.exDate);
		}
		return responseList;
	}
}
