package com.study.petory.domain.pet.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
@Table(name = "tb_pet")
@NoArgsConstructor
public class Pet extends TimeFeatureBasedEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column
	private String size;

	@Column
	private String species;

	@Column
	private String gender;

	@Column
	private String birthday;

	private String photo;

	@JsonBackReference
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@Builder
	public Pet(String name, String size, String species, String gender, String birthday, User user) {
		this.name = name;
		this.size =size;
		this.species = species;
		this.gender = gender;
		this.birthday = birthday;
		this.user = user;
	}

	public void updatePetInfo(String name, String size, String gender) {
		this.name = name;
		this.size = size;
		this.gender = gender;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
