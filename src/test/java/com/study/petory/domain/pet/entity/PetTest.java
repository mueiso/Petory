package com.study.petory.domain.pet.entity;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.entity.UserPrivateInfo;
import com.study.petory.domain.user.entity.UserRole;
import com.study.petory.domain.user.entity.UserStatus;
import com.study.petory.domain.user.entity.Role;

class PetTest {

	@Test
	void 펫_생성_성공() {

		// given
		String name = "쿠키";
		PetSize size = PetSize.SMALL;
		String species = "푸들";
		String gender = "남";
		String birthday = "2022-01-01";
		User user = createUserWithStatus(UserStatus.ACTIVE);

		// when
		Pet pet = Pet.builder()
			.name(name)
			.size(size)
			.species(species)
			.gender(gender)
			.birthday(birthday)
			.user(user)
			.build();

		// then
		assertThat(pet.getName()).isEqualTo(name);
		assertThat(pet.getSize()).isEqualTo(size);
		assertThat(pet.getSpecies()).isEqualTo(species);
		assertThat(pet.getGender()).isEqualTo(gender);
		assertThat(pet.getBirthday()).isEqualTo(birthday);
		assertThat(pet.getUser()).isEqualTo(user);
	}

	@Test
	void 펫_정보_수정_성공() {

		// given: 초기 Pet 객체 구성
		User user = createUserWithStatus(UserStatus.ACTIVE);
		Pet pet = Pet.builder()
			.name("쿠키")
			.size(PetSize.MEDIUM)
			.gender("남")
			.birthday("2020-01-01")
			.user(user)
			.build();

		// when: updatePetInfo 메서드로 정보 수정
		pet.updatePetInfo("초코", "남자였던 것", "2021-05-05");

		// then: 수정된 값들이 정상적으로 반영되었는지 확인
		assertThat(pet.getName()).isEqualTo("초코");
		assertThat(pet.getGender()).isEqualTo("남자였던 것");
		assertThat(pet.getBirthday()).isEqualTo("2021-05-05");
	}

	@Test
	void 펫_소유자_확인_성공() {

		// given: 사용자와 펫 객체 생성
		User user = createUserWithStatus(UserStatus.ACTIVE);
		Pet pet = Pet.builder()
			.name("쿠키")
			.size(PetSize.SMALL)
			.user(user)
			.build();

		// when: user 의 ID와 펫의 owner 일치 여부 확인
		boolean isOwner = pet.isPetOwner(1L); // createUserWithStatus 에서 ID를 1L로 세팅함

		// then: isPetOwner 결과가 true 여야 함
		assertThat(isOwner).isTrue();
	}

	@Test
	void 펫_소유자_설정_성공() {

		// given: 유저와 소유자 미지정된 Pet 객체 생성
		User user = createUserWithStatus(UserStatus.ACTIVE);
		Pet pet = Pet.builder()
			.name("쿠키")
			.size(PetSize.SMALL)
			.build();

		// when: setUser()로 사용자 설정
		pet.setUser(user);

		// then: 연관된 유저가 정상적으로 설정되었는지 확인
		assertThat(pet.getUser()).isEqualTo(user);
	}

	// 중복 코드 줄이기 위한 테스트용 유저 객체 생성 유틸 메서드
	private User createUserWithStatus(UserStatus status) {

		UserPrivateInfo privateInfo = UserPrivateInfo.builder()
			.authId("google")
			.name("나이름")
			.mobileNum("01012345678")
			.build();

		User user = User.builder()
			.nickname("닉네임")
			.email("test@email.com")
			.userPrivateInfo(privateInfo)
			.userRole(new ArrayList<>(List.of(
				UserRole.builder().role(Role.USER).build()
			)))
			.build();

		user.updateStatus(status);

		// 테스트 ID 강제 주입
		ReflectionTestUtils.setField(user, "id", 1L);

		return user;
	}
}
