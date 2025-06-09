package com.study.petory.domain.dailyQna.entity;

import com.study.petory.common.entity.DeletionBasedEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
public class Question extends DeletionBasedEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String question;

	@Column(nullable = false, unique = true, length = 10)
	private String date;

	@Builder
	public Question(String question, String date) {
		this.question = question;
		this.date = date;
	}

	public void update(String question, String date) {
		if (question != null) {
			this.question = question;
		}
		if (date != null) {
			this.date = date;
		}
	}

	// questionId 검증 메서드
	public boolean isEqualId(Long questionId) {
		return this.id.equals(questionId);
	}
}
