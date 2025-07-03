package com.study.petory.domain.ownerboard.entity;

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
@Table(name = "tb_owner_board_image")
public class OwnerBoardImage extends CreationBasedEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String url;

	@Setter // 양방향 연관관계 설정을 위한 setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "owner_board_id")
	@JsonIgnore
	private OwnerBoard ownerBoard;

	public OwnerBoardImage(String url, OwnerBoard ownerBoard) {
		this.url = url;
		this.ownerBoard = ownerBoard;
	}

	public Long getOwnerBoardId() {
		return this.ownerBoard != null ? this.getOwnerBoard().getId() : null;
	}

}
