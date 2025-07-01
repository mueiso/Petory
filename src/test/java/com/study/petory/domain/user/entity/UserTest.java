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
	// 테스트 시작 전에 항상 실행되는 초기화 메서드
	void setUp() {

		// UserPrivateInfo 빌더로 객체 생성
		UserPrivateInfo privateInfo = UserPrivateInfo.builder()
			.authId("google")
			.name("나이름")
			.mobileNum("01012345678")
			.build();

		// UserRole 객체 생성 (ENUM 타입)
		UserRole role = UserRole.builder()
			.role(Role.USER)
			.build();

		// User 객체 생성, 필요한 필드만 초기화
		user = User.builder()
			.nickname("닉네임")
			.email("test@example.com")
			.userPrivateInfo(privateInfo)
			.userRole(List.of(role))
			.build();
	}

	@Test
	void updateStatus_변경() {

		// userStatus 를 SUSPENDED 로 변경하는 테스트
		user.updateStatus(UserStatus.SUSPENDED);
		// 변경 여부 검증
		assertThat(user.getUserStatus()).isEqualTo(UserStatus.SUSPENDED);
	}

	@Test
	void activateUser_상태가_ACTIVE로_변경() {

		// userStatus 를 DELETED 로 변경한 뒤, 다시 활성화
		user.updateStatus(UserStatus.DELETED);
		// restoreEntity() + 상태 ACTIVE 로 변경
		user.activateUser();
		assertThat(user.getUserStatus()).isEqualTo(UserStatus.ACTIVE);
	}

	@Test
	void updateNickname_닉네임_변경() {

		// 닉네임을 새 값으로 업데이트
		user.updateNickname("새로운 닉네임");
		// 변경 확인
		assertThat(user.getNickname()).isEqualTo("새로운 닉네임");
	}

	@Test
	void updateLastLoginAt_로그인시간_저장() {

		// 로그인 시간을 현재 시간으로 기록
		LocalDateTime now = LocalDateTime.now();

		// 저장된 시간 확인
		user.updateLastLoginAt(now);
		assertThat(user.getLastLoginAt()).isEqualTo(now);
	}

	@Test
	void isEqualId_같은_id일때_true() {

		// private 필드인 id를 강제로 1L로 설정
		Long id = 1L;
		ReflectionTestUtils.setField(user, "id", id);

		// 동일한 ID일 때 true 확인
		assertThat(user.isEqualId(1L)).isTrue();
	}

	@Test
	void isEqualId_다른_id일때_false() {

		// id를 2L로 설정한 후 1L과 비교
		ReflectionTestUtils.setField(user, "id", 2L);

		// 다르면 false
		assertThat(user.isEqualId(1L)).isFalse();
	}

	@Test
	void hasRole_포함된_역할이면_true() {

		// 생성 시 Role.USER 포함했으므로 true 예상
		assertThat(user.hasRole(Role.USER)).isTrue();
	}

	@Test
	void addTradeBoard_정상추가되고_연관관계설정() {

		// TradeBoard 객체를 생성하고 추가
		TradeBoard tradeBoard = new TradeBoard();
		user.addTradeBoard(tradeBoard);

		// 리스트에 포함됐는지 확인
		assertThat(user.getTradeBoards()).contains(tradeBoard);
		// 연관관계 설정 확인
		assertThat(tradeBoard.getUser()).isEqualTo(user);
	}

	@Test
	void addDailyQna_정상추가되고_연관관계설정() {

		// DailyQna 객체를 생성하고 추가
		DailyQna dailyQna = new DailyQna();
		user.addDailyQna(dailyQna);

		// 리스트에 포함됐는지 확인
		assertThat(user.getDailyQnas()).contains(dailyQna);
		// 연관관계 설정 확인
		assertThat(dailyQna.getUser()).isEqualTo(user);
	}

	@Test
	void addAlbum_정상추가되고_연관관계설정() {

		// Album 객체를 생성하고 추가
		Album album = new Album();
		user.addAlbum(album);

		// 리스트에 포함됐는지 확인
		assertThat(user.getAlbums()).contains(album);
		// 연관관계 설정 확인
		assertThat(album.getUser()).isEqualTo(user);
	}

	@Test
	void addPet_정상추가되고_연관관계설정() {

		// Pet 객체를 생성하고 추가
		Pet pet = new Pet();
		user.addPet(pet);

		// 리스트에 포함됐는지 확인
		assertThat(user.getPets()).contains(pet);
		// 연관관계 설정 확인
		assertThat(pet.getUser()).isEqualTo(user);
	}
}
