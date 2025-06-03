package com.study.petory.domain.ownerBoardComment.entity;

import com.study.petory.common.entity.BaseEntityWithBothAt;
import com.study.petory.domain.ownerBoard.entity.OwnerBoard;
import com.study.petory.domain.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "tb_owner_board_comment")
public class OwnerBoardComment extends BaseEntityWithBothAt {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String content;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "owner_board_id")
	private OwnerBoard ownerBoard;

	@Builder
	public OwnerBoardComment(String content, User user, OwnerBoard ownerBoard) {
		this.content = content;
		this.user = user;
		this.ownerBoard = ownerBoard;
	}

	public void updateContent(String content) {this.content = content;}
}
