package com.study.petory.domain.user.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.study.petory.common.entity.TimeFeatureBasedEntity;
import com.study.petory.domain.album.entity.Album;
import com.study.petory.domain.dailyQna.entity.DailyQna;
import com.study.petory.domain.pet.entity.Pet;
import com.study.petory.domain.tradeBoard.entity.TradeBoard;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
	name = "tb_user",
	indexes = {
		@Index(name = "idx_user_email", columnList = "email"),
		@Index(name = "idx_user_status_deleted_at", columnList = "user_status, deleted_at"),
		@Index(name = "idx_user_status_updated_at", columnList = "user_status, updated_at")
	}
)
@NoArgsConstructor
public class User extends TimeFeatureBasedEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String nickname;

	@Column(nullable = false)
	private String email;

	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "user_private_info_id")
	private UserPrivateInfo userPrivateInfo;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "user_id")
	private List<UserRole> userRole;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private UserStatus userStatus = UserStatus.ACTIVE;

	@Column
	private LocalDateTime lastLoginAt;

	@JsonManagedReference
	@OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
	private List<TradeBoard> tradeBoards = new ArrayList<>();

	@JsonManagedReference
	@OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
	private List<DailyQna> dailyQnas = new ArrayList<>();

	@JsonManagedReference
	@OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
	private List<Album> albums = new ArrayList<>();

	@JsonManagedReference
	@OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
	private List<Pet> pets = new ArrayList<>();

	@Builder
	public User(String nickname, String email, UserPrivateInfo userPrivateInfo, List<UserRole> userRole) {
		this.email = email;
		this.nickname = nickname;
		this.userPrivateInfo = userPrivateInfo;
		this.userRole = userRole;
	}

	public void updateStatus(UserStatus userStatus) {
		this.userStatus = userStatus;
	}

	public void activateUser() {
		this.restoreEntity();
		this.updateStatus(UserStatus.ACTIVE);
	}

	public void updateNickname(String newNickname) {
		this.nickname = newNickname;
	}

	// 로그인 시간 기록용
	public void updateLastLoginAt(LocalDateTime time) {
		this.lastLoginAt = time;
	}

	// userId 검증 메서드
	public boolean isEqualId(Long userId) {
		return userId != null && userId.equals(this.id);
	}

	public boolean hasRole(Role role) {
		return userRole.stream().anyMatch(userRole -> userRole.getRole().equals(role));
	}

	// 연관관계 편의 메서드
	public void addTradeBoard(TradeBoard tradeBoard) {
		tradeBoards.add(tradeBoard);
		tradeBoard.setUser(this);  // 주인 객체 설정
	}

	public void addDailyQna(DailyQna dailyQna) {
		this.dailyQnas.add(dailyQna);
		dailyQna.setUser(this);
	}

	public void addAlbum(Album album) {
		this.albums.add(album);
		album.setUser(this);
	}

	public User(Long id) {
		this.id = id;
	}

	public void addPet(Pet pet) {
		this.pets.add(pet);
		pet.setUser(this);
	}
}
