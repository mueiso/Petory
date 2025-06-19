package com.study.petory.domain.notification.entity;

import com.study.petory.common.entity.CreationBasedEntity;
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
@Table(name = "tb_notification")
@Getter
@NoArgsConstructor
public class Notification extends CreationBasedEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@Column(nullable = false)
	private String content;

	@Builder
	public Notification(User user, String content) {
		this.user = user;
		this.content = content;
	}

	public Long getUserId() {
		return this.user.getId();
	}
}
