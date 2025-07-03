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
