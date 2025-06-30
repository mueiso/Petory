package com.study.petory.domain.ownerBoard.entity;

import org.hibernate.annotations.Where;

import com.study.petory.common.entity.TimeFeatureBasedEntity;
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
@Where(clause = "deleted_at IS NULL")
public class OwnerBoardComment extends TimeFeatureBasedEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String content;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = true)
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

	public void updateContent(String content) {
		this.content = content;
	}

	// ownerBoardCommentId 검증 메서드
	public boolean isEqualId(Long ownerBoardCommentId) {
		return this.id.equals(ownerBoardCommentId);
	}

	// user 검증 메서드
	public boolean isEqualUser(Long userId) {
		if (this.user == null) {
			return false;
		}
		return this.user.isEqualId(userId);
	}

	// ownerBoard 검증 메서드
	public boolean isEqualOwnerBoard(Long ownerBoardId) {
		return this.ownerBoard.isEqualId(ownerBoardId);
	}

	// 연관관계 참조 끊기 위한 메서드
	public void setUser(User user) {
		this.user = user;
	}
}
