package com.study.petory.domain.pet.entity;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.study.petory.common.entity.TimeFeatureBasedEntity;
import com.study.petory.domain.user.entity.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "tb_pet")
@NoArgsConstructor
@DynamicUpdate
public class Pet extends TimeFeatureBasedEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private PetSize size;

	@Column
	private String species;

	@Column
	private String gender;

	@Column
	private String birthday;

	@OneToMany(mappedBy = "pet", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<PetImage> images = new ArrayList<>();

	@JsonBackReference
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@Builder
	public Pet(String name, PetSize size, String species, String gender, String birthday, User user) {
		this.name = name;
		this.size = size;
		this.species = species;
		this.gender = gender;
		this.birthday = birthday;
		this.user = user;
	}

	public void updatePetInfo(String name, String gender, String birthday) {
		this.name = name;
		this.gender = gender;
		this.birthday = birthday;
	}

	public boolean isPetOwner(Long userId) {
		return this.user != null && this.user.isEqualId(userId);
	}

	public void setUser(User user) {
		this.user = user;
	}
}
