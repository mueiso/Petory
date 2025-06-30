package com.study.petory.domain.album.entity;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.study.petory.common.entity.UpdateBasedEntity;
import com.study.petory.domain.user.entity.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@DynamicUpdate
@Table(name = "tb_album")
@NoArgsConstructor
public class Album extends UpdateBasedEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JsonBackReference
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(columnDefinition = "TEXT")
	private String content;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private AlbumVisibility albumVisibility;

	@OneToMany(mappedBy = "album", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<AlbumImage> albumImageList = new ArrayList<>();

	@Builder
	public Album(User user, String content, AlbumVisibility albumVisibility) {
		this.user = user;
		this.content = content;
		this.albumVisibility = albumVisibility;
	}

	public boolean isEqualUser(Long userId) {
		return this.user.isEqualId(userId);
	}

	public void updateAlbum(String content) {
		this.content = content;
	}

	public void updateVisibility(AlbumVisibility albumVisibility) {
		this.albumVisibility = albumVisibility;
	}

	public String getFirstUrl() {
		return this.albumImageList.get(0).getUrl();
	}

	public void setUser(User user) {
		this.user = user;
	}
}
