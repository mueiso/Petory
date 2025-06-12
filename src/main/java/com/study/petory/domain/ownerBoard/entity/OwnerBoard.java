package com.study.petory.domain.ownerBoard.entity;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SQLRestriction;

import com.study.petory.common.entity.TimeFeatureBasedEntity;
import com.study.petory.domain.user.entity.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "tb_owner_board")
@NoArgsConstructor
@SQLRestriction("deleted_at is NULL")
@DynamicUpdate
public class OwnerBoard extends TimeFeatureBasedEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 100)
	private String title;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String content;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@OneToMany(mappedBy = "ownerBoard", cascade = CascadeType.ALL)
	private List<OwnerBoardComment> comments = new ArrayList<>();

	@OneToMany(mappedBy = "ownerBoard", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<OwnerBoardImage> images = new ArrayList<>();

	@Builder
	public OwnerBoard(String title, String content, User user) {
		this.title = title;
		this.content = content;
		this.user = user;
	}

	public void updateOwnerBoard(String title, String content) {
		this.title = title;
		this.content = content;
	}

	// 양방향 연관관계 편의 메서드
	public void addImage(OwnerBoardImage image) {
		images.add(image);
		image.setOwnerBoard(this);
	}

	// ownerBoardId 검증 메서드
	public boolean isEqualId(Long ownerBoardId) {
		return this.id.equals(ownerBoardId);
	}

	// user 검증 메서드
	public boolean isEqualUser(Long userId) {
		return this.user.isEqualId(userId);
	}
}

