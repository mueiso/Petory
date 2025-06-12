package com.study.petory.domain.dailyQna.entity;

import org.hibernate.annotations.Where;

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
	name = "tb_daily_qna",
	indexes = {
		@Index(name = "index_user_question",columnList = "user_id, question_id"),
	}
	)
@NoArgsConstructor
public class DailyQna extends TimeFeatureBasedEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JoinColumn(name = "user_id", nullable = false)
	@ManyToOne(fetch = FetchType.LAZY)
	private User user;

	@JoinColumn(name = "question_id", nullable = false)
	@ManyToOne(fetch = FetchType.LAZY)
	private Question question;

	@Column(nullable = false)
	private String answer;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private DailyQnaStatus dailyQnaStatus;

	@Builder
	public DailyQna(User user, Question question, String answer, DailyQnaStatus dailyQnaStatus) {
		this.user = user;
		this.question = question;
		this.answer = answer;
		this.dailyQnaStatus = dailyQnaStatus;
	}

	public void updateDailyQna(String answer) {
		this.answer = answer;
	}

	// user 검증 메서드
	public boolean isEqualUser(Long userId) {
		return this.user.isEqualId(userId);
	}

	public void updateStatusActive() {
		this.dailyQnaStatus = DailyQnaStatus.ACTIVE;
	}
	public void updateStatusHidden() {
		this.dailyQnaStatus = DailyQnaStatus.HIDDEN;
	}
	public void updateStatusDelete() {
		this.dailyQnaStatus = DailyQnaStatus.DELETED;
	}
}
