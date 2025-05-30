package com.study.petory.domain.tradeBoard.entity;

import com.study.petory.common.entity.BaseEntityWithBothAt;
import com.study.petory.domain.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "trade_board")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TradeBoard extends BaseEntityWithBothAt {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private TradeCategory category;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false)
	private String content;

	private String photoUrl;

	@Column(nullable = false)
	private Integer price;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
}
