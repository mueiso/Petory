package com.study.petory.domain.ownerBoard.entity;

import com.study.petory.common.entity.BaseEntityWithBothAt;
import com.study.petory.domain.user.entity.User;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class OwnerBoard extends BaseEntityWithBothAt {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String title;

	private String content;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;



}
