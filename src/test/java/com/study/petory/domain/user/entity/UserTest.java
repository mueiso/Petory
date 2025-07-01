package com.study.petory.domain.user.entity;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.study.petory.domain.album.entity.Album;
import com.study.petory.domain.dailyQna.entity.DailyQna;
import com.study.petory.domain.pet.entity.Pet;
import com.study.petory.domain.tradeBoard.entity.TradeBoard;

class UserTest {

	private User user;

	@BeforeEach
	void setUp() {

		UserPrivateInfo privateInfo = UserPrivateInfo.builder()
			.authId("google")
			.name("나이름")
			.mobileNum("01012345678")
			.build();

		UserRole role = UserRole.builder()
			.role(Role.USER)
			.build();

		user = User.builder()
			.nickname("닉네임")
			.email("test@example.com")
			.userPrivateInfo(privateInfo)
			.userRole(List.of(role))
			.build();
	}

	@Test
	void updateStatus_변경() {

		user.updateStatus(UserStatus.SUSPENDED);
		assertThat(user.getUserStatus()).isEqualTo(UserStatus.SUSPENDED);
	}

	@Test
	void activateUser_상태가_ACTIVE로_변경() {

		user.updateStatus(UserStatus.DELETED);
		user.activateUser();
		assertThat(user.getUserStatus()).isEqualTo(UserStatus.ACTIVE);
	}

	@Test
	void updateNickname_닉네임_변경() {

		user.updateNickname("새로운 닉네임");
		assertThat(user.getNickname()).isEqualTo("새로운 닉네임");
	}

	@Test
	void updateLastLoginAt_로그인시간_저장() {

		LocalDateTime now = LocalDateTime.now();
		user.updateLastLoginAt(now);
		assertThat(user.getLastLoginAt()).isEqualTo(now);
	}

	@Test
	void isEqualId_같은_id일때_true() {

		Long id = 1L;
		ReflectionTestUtils.setField(user, "id", id);
		assertThat(user.isEqualId(1L)).isTrue();
	}

	@Test
	void isEqualId_다른_id일때_false() {

		ReflectionTestUtils.setField(user, "id", 2L);
		assertThat(user.isEqualId(1L)).isFalse();
	}

	@Test
	void hasRole_포함된_역할이면_true() {

		assertThat(user.hasRole(Role.USER)).isTrue();
	}

	@Test
	void addTradeBoard_정상추가되고_연관관계설정() {

		TradeBoard tradeBoard = new TradeBoard();
		user.addTradeBoard(tradeBoard);
		assertThat(user.getTradeBoards()).contains(tradeBoard);
		assertThat(tradeBoard.getUser()).isEqualTo(user);
	}

	@Test
	void addDailyQna_정상추가되고_연관관계설정() {

		DailyQna dailyQna = new DailyQna();
		user.addDailyQna(dailyQna);
		assertThat(user.getDailyQnas()).contains(dailyQna);
		assertThat(dailyQna.getUser()).isEqualTo(user);
	}

	@Test
	void addAlbum_정상추가되고_연관관계설정() {

		Album album = new Album();
		user.addAlbum(album);
		assertThat(user.getAlbums()).contains(album);
		assertThat(album.getUser()).isEqualTo(user);
	}

	@Test
	void addPet_정상추가되고_연관관계설정() {

		Pet pet = new Pet();
		user.addPet(pet);
		assertThat(user.getPets()).contains(pet);
		assertThat(pet.getUser()).isEqualTo(user);
	}
}
