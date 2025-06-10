package com.study.petory.domain.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "tb_user_role")
@NoArgsConstructor
public class UserRole {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Role role;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@Builder
	public UserRole(Role role) {
		this.role = role;
	}

	// @Builder
	// public UserRole(Role role, User user) {
	// 	this.role = role;
	// }
}
