package com.study.petory.domain.dailyqna.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(
	name = "tb_daily_answer",
	indexes = {
		@Index(name = "index_user_question", columnList = "user_id, daily_question_id"),
	}
)
@NoArgsConstructor
public class DailyAnswer extends TimeFeatureBasedEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JsonBackReference
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@JoinColumn(name = "daily_question_id", nullable = false)
	@ManyToOne(fetch = FetchType.LAZY)
	private DailyQuestion dailyQuestion;

	@Column(nullable = false)
	private String answer;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private DailyAnswerStatus dailyAnswerStatus;

	@Builder
	public DailyAnswer(User user, DailyQuestion dailyQuestion, String answer, DailyAnswerStatus dailyAnswerStatus) {
		this.user = user;
		this.dailyQuestion = dailyQuestion;
		this.answer = answer;
		this.dailyAnswerStatus = dailyAnswerStatus;
	}

	public void updateDailyQna(String answer) {
		this.answer = answer;
	}

	public boolean isEqualUser(Long userId) {
		return this.user.isEqualId(userId);
	}

	public void updateStatusActive() {
		this.dailyAnswerStatus = DailyAnswerStatus.ACTIVE;
	}

	public void updateStatusHidden() {
		this.dailyAnswerStatus = DailyAnswerStatus.HIDDEN;
	}

	public void updateStatusDelete() {
		this.dailyAnswerStatus = DailyAnswerStatus.DELETED;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
