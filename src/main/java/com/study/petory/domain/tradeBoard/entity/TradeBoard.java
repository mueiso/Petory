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
@Table(name = "tb_trade_board")
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

	public void updateCategory(TradeCategory tradeCategory) {
		this.category = tradeCategory;
	}

	public void updateTitle(String title) {
		this.title = title;
	}

	public void updateContent(String content) {
		this.content = content;
	}

	public void updatePhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}

	public void updatePrice(Integer price) {
		this.price = price;
	}
}
