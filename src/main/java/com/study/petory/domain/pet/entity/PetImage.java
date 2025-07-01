package com.study.petory.domain.pet.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.study.petory.common.entity.CreationBasedEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "tb_pet_image")
public class PetImage extends CreationBasedEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String url;

	@Setter // 양방향 연관관계 설정을 위한 setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pet_id")
	@JsonIgnore
	private Pet pet;

	public PetImage(String url, Pet pet) {
		this.url = url;
		this.pet = pet;
	}
}
