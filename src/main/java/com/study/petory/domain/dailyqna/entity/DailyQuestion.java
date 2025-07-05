package com.study.petory.domain.dailyqna.entity;

import com.study.petory.common.entity.TimeFeatureBasedEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "tb_daily_question")
@NoArgsConstructor
public class DailyQuestion extends TimeFeatureBasedEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String question;

	@Column(nullable = false, unique = true, length = 10)
	private String date;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private DailyQuestionStatus dailyQuestionStatus;

	@Builder
	public DailyQuestion(String question, String date, DailyQuestionStatus dailyQuestionStatus) {
		this.question = question;
		this.date = date;
		this.dailyQuestionStatus = dailyQuestionStatus;
	}

	public void update(String question, String date) {
		this.question = question;
		this.date = date;
	}

	public boolean isDeleted() {
		return this.getDeletedAt() != null;
	}

	public void updateStatusActive() {
		this.dailyQuestionStatus = DailyQuestionStatus.ACTIVE;
	}

	public void updateStatusInactive() {
		this.dailyQuestionStatus = DailyQuestionStatus.INACTIVE;
	}

	public void updateStatusDelete() {
		this.dailyQuestionStatus = DailyQuestionStatus.DELETED;
	}
}
