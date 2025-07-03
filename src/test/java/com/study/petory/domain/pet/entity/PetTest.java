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

		/* [given]
		 * userStatus ACTIVE 인 테스트용 유저 생성
		 */
		User user = createUserWithStatus(UserStatus.ACTIVE);

		/* [when]
		 * Pet 객체 생성
		 */
		Pet pet = createDefaultPetBuilder(user).build();

		/* [then]
		 * 모든 필드가 기대한 값으로 정확히 세팅됐는지 검증
		 */
		assertThat(pet.getName()).isEqualTo("쿠키");
		assertThat(pet.getSize()).isEqualTo(PetSize.SMALL);
		assertThat(pet.getSpecies()).isEqualTo("푸들");
		assertThat(pet.getGender()).isEqualTo("남");
		assertThat(pet.getBirthday()).isEqualTo("2022-01-01");
		assertThat(pet.getUser()).isEqualTo(user);
	}

	@Test
	void 펫_정보_수정_성공() {

		/* [given]
		 * userStatus ACTIVE 인 테스트용 유저 생성
		 * Pet 객체 생성
		 */
		User user = createUserWithStatus(UserStatus.ACTIVE);
		Pet pet = createDefaultPetBuilder(user).build();

		/* [when]
		 * updateInfo() 메서드를 통해 필드 값 변경
		 */
		pet.updatePetInfo("쿠키", "남자였던 것", "2021-05-05");

		/* [then]
		 * 변경된 값들이 정상적으로 반영되었는지 검증
		 */
		assertThat(pet.getName()).isEqualTo("쿠키");
		assertThat(pet.getGender()).isEqualTo("남자였던 것");
		assertThat(pet.getBirthday()).isEqualTo("2021-05-05");
	}

	@Test
	void 펫_소유자_확인_성공() {

		/* [given]
		 * userStatus ACTIVE 인 테스트용 유저 생성
		 * Pet 객체 생성
		 */
		User user = createUserWithStatus(UserStatus.ACTIVE);
		Pet pet = createDefaultPetBuilder(user).build();

		/* [when]
		 * Pet 의 소유자가 주어진 userId와 일치하는지 확인
		 * 테스트에서 강제로 userId를 1L로 설정
		 */
		boolean isOwner = pet.isPetOwner(1L);

		/* [then]
		 * true 여야 테스트 통과
		 */
		assertThat(isOwner).isTrue();
	}

	@Test
	void 펫_소유자_설정_성공() {

		// given
		User user = createUserWithStatus(UserStatus.ACTIVE);
		Pet pet = Pet.builder()
			.name("쿠키")
			.size(PetSize.SMALL)
			.build();

		// when
		pet.setUser(user);

		// then
		assertThat(pet.getUser()).isEqualTo(user);
	}

	// 중복 코드 제거용 테스트용 기본 Pet 빌더 생성 메서드
	private Pet.PetBuilder createDefaultPetBuilder(User user) {
		return Pet.builder()
			.name("쿠키")
			.size(PetSize.SMALL)
			.species("푸들")
			.gender("남")
			.birthday("2022-01-01")
			.user(user);
	}

	// 중복 코드 제거용 테스트용 유저 생성 유틸 메서드
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

		// ID는 테스트 환경에서 명시적으로 설정
		ReflectionTestUtils.setField(user, "id", 1L);

		return user;
	}
}
