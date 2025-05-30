package com.study.petory.domain.user.entity;

import java.util.List;

import com.study.petory.common.entity.BaseEntityWithBothAt;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "user")
@NoArgsConstructor
public class User extends BaseEntityWithBothAt {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String nickname;

	@Column(nullable = false)
	private String email;

	@OneToOne
	@JoinColumn(name = "user_private_info_id")
	private UserPrivateInfo userPrivateInfo;

	@OneToMany(mappedBy = "user")
	private List<UserRole> userRole;
}
