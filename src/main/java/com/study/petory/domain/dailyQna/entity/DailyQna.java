package com.study.petory.domain.dailyQna.entity;

import org.hibernate.annotations.Where;

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
@Table(name = "tb_daily_qna")
@Where(clause = "deleted_at is null")
@NoArgsConstructor
public class DailyQna extends TimeFeatureBasedEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JoinColumn(nullable = false)
	@ManyToOne(fetch = FetchType.LAZY)
	private User user;

	@JoinColumn(nullable = false)
	@ManyToOne(fetch = FetchType.LAZY)
	private Question question;

	@Column(nullable = false, length = 255)
	private String answer;

	@Builder
	public DailyQna(User user, Question question, String answer) {
		this.user = user;
		this.question = question;
		this.answer = answer;
	}

	public void updateDailyQna(String answer) {
		this.answer = answer;
	}

	// user 검증 메서드
	public boolean isEqualUser(Long userId) {
		return this.user.isEqualId(userId);
	}

	// question 검증 메서드
	public boolean isEqualQuestion(Long questionId) {
		return this.question.isEqualId(questionId);
	}
}
