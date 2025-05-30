package com.study.petory.domain.place.entity;

import com.study.petory.common.entity.BaseEntityWithBothAt;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "place")
@NoArgsConstructor
public class Place extends BaseEntityWithBothAt {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
}
