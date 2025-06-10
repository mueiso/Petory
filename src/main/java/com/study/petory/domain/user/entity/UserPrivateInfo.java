package com.study.petory.domain.user.entity;

import com.study.petory.common.entity.TimeFeatureBasedEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "tb_user_private_info")
@NoArgsConstructor
public class UserPrivateInfo extends TimeFeatureBasedEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String authId;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String mobileNum;

	@Builder
	public UserPrivateInfo(String authId, String name, String mobileNum) {
		this.authId = authId;
		this.name = name;
		this.mobileNum = mobileNum;
	}
}
