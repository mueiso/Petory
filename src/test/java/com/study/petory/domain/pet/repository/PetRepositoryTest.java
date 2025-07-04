package com.study.petory.domain.pet.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.study.petory.common.config.QueryDSLConfig;
import com.study.petory.domain.pet.entity.Pet;
import com.study.petory.domain.pet.entity.PetSize;
import com.study.petory.domain.user.entity.Role;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.entity.UserPrivateInfo;
import com.study.petory.domain.user.entity.UserRole;
import com.study.petory.domain.user.repository.UserRepository;

@Import(QueryDSLConfig.class)
@DataJpaTest(
	excludeAutoConfiguration = {
		org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration.class,
		org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration.class
	}
)
@EntityScan(basePackages = "com.study.petory.domain")
class PetRepositoryTest {

	@Autowired
	private PetRepository petRepository;

	@Autowired
	private UserRepository userRepository;

	@Test
	void findPetById_조회_성공() {

		// given
		User savedUser = createAndSaveUser();

		Pet pet = Pet.builder()
			.name("쿠키")
			.size(PetSize.SMALL)
			.species("푸들")
			.gender("남")
			.birthday("2022-01-01")
			.build();

		pet.setUser(savedUser);
		petRepository.save(pet);

		// when
		Pet foundPet = petRepository.findPetById(pet.getId())
			.orElseThrow(() -> new RuntimeException("반려동물 조회 실패"));

		// then
		assertThat(foundPet.getName()).isEqualTo("쿠키");
		assertThat(foundPet.getUser()).isEqualTo(savedUser);
	}

	@Test
	void findAllByUser_페이징된_반려동물_목록조회_성공() {

		// given
		User user = createAndSaveUser();
		Pet pet1 = Pet.builder().name("쿠키").size(PetSize.SMALL).user(user).build();
		Pet pet2 = Pet.builder().name("뽀삐").size(PetSize.MEDIUM).user(user).build();
		petRepository.saveAll(List.of(pet1, pet2));

		PageRequest pageRequest = PageRequest.of(0, 10);

		// when
		Page<Pet> result = petRepository.findAllByUser(user, pageRequest);

		// then
		assertThat(result.getTotalElements()).isEqualTo(2);
		assertThat(result.getContent()).extracting("name")
			.containsExactlyInAnyOrder("쿠키", "뽀삐");
	}

	// 테스트용 유저 생성 및 저장 위한 유틸 메서드
	private User createAndSaveUser() {

		UserPrivateInfo privateInfo = UserPrivateInfo.builder()
			.authId("google123")
			.name("나이름")
			.mobileNum("01012345678")
			.build();

		User user = User.builder()
			.nickname("닉네임")
			.email("test@email.com")
			.userPrivateInfo(privateInfo)
			.userRole(List.of(UserRole.builder().role(Role.USER).build()))
			.build();

		return userRepository.save(user);
	}
}
