package com.study.petory.domain.calender.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "calender")
@NoArgsConstructor
public class Calender {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
}
