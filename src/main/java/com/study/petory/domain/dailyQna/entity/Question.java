package com.study.petory.domain.dailyQna.entity;

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
@Table(name = "tb_question")
@NoArgsConstructor
public class Question extends TimeFeatureBasedEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String question;

	@Column(nullable = false, unique = true, length = 10)
	private String date;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private QuestionStatus questionStatus;

	@Builder
	public Question(String question, String date, QuestionStatus questionStatus) {
		this.question = question;
		this.date = date;
		this.questionStatus = questionStatus;
	}

	public void update(String question, String date) {
		this.question = question;
		this.date = date;
	}

	public boolean isDeleted() {
		return this.getDeletedAt() != null;
	}

	public void updateStatusActive() {
		this.questionStatus = QuestionStatus.ACTIVE;
	}

	public void updateStatusInactive() {
		this.questionStatus = QuestionStatus.INACTIVE;
	}

	public void updateStatusDelete() {
		this.questionStatus = QuestionStatus.DELETED;
	}
}
