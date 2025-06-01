package com.study.petory.domain.dailyQna.entity;

import com.study.petory.common.entity.BaseEntityWithBothAt;
import com.study.petory.domain.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "daily_qna")
@NoArgsConstructor
public class DailyQna extends BaseEntityWithBothAt {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JoinColumn(nullable = false)
	@ManyToOne
	private User userId;

	@JoinColumn(nullable = false)
	@ManyToOne
	private Question questionId;

	@Column(nullable = false, length = 255)
	private String answer;

	public DailyQna(User userId, Question questionId, String answer) {
		this.userId = userId;
		this.questionId = questionId;
		this.answer = answer;
	}
}
