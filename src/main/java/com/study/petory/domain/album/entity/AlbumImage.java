package com.study.petory.domain.album.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.study.petory.common.entity.CreationBasedEntity;

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
@Table(name = "tb_album_image")
@NoArgsConstructor
public class AlbumImage extends CreationBasedEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "album_id", nullable = false)
	private Album album;

	@Column(nullable = false)
	private String url;

	@Builder
	public AlbumImage(String url, Album album) {
		this.album = album;
		this.url = url;
	}
}
