package com.study.petory.domain.place.entity;

import com.study.petory.common.entity.BaseEntityWithBothAt;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

// @Getter
@Entity
// @Table(name = "place")
public class Place extends BaseEntityWithBothAt {

	@Id
	private Long id;

}
