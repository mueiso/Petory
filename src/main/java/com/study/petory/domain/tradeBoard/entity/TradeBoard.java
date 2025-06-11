package com.study.petory.domain.tradeBoard.entity;

import org.hibernate.annotations.Where;

import com.study.petory.common.entity.TimeFeatureBasedEntity;
import com.study.petory.domain.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "tb_trade_board")
@NoArgsConstructor
@Where(clause = "status != 'DELETED'")
public class TradeBoard extends TimeFeatureBasedEntity {

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

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TradeBoardStatus status;

	@ManyToOne
	@JoinColumn(name = "tb_user_id")
	private User user;

	@Builder
	public TradeBoard(TradeCategory category, String title, String content, String photoUrl, Integer price, User user) {
		this.category = category;
		this.title = title;
		this.content = content;
		this.photoUrl = photoUrl;
		this.price = price;
		this.status = TradeBoardStatus.FOR_SALE;
		this.user = user;
	}

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

	// tradeBoardId 검증 메서드
	public boolean isEqualId(Long tradeBoardId) {
		return this.id.equals(tradeBoardId);
	}

	// user 검증 메서드
	public boolean isEqualUser(Long userId) {
		return this.user.isEqualId(userId);
	}

	public void updateStatus(TradeBoardStatus status) {
		this.status = status;
	}
}
