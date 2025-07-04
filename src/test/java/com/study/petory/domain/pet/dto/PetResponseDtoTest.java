package com.study.petory.domain.pet.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.study.petory.domain.pet.entity.Pet;
import com.study.petory.domain.pet.entity.PetSize;
import com.study.petory.domain.user.entity.User;

class PetResponseDtoTest {

	@Test
	void of_정상적으로_DTO_생성됨() {

		// given
		User user = new User(1L);  // 최소한의 유저 객체 (ID만 설정)

		Pet pet = Pet.builder()
			.name("코코")
			.size(PetSize.SMALL)
			.species("푸들")
			.gender("암컷")
			.birthday("2022-01-01")
			.user(user)
			.build();

		// createdAt 설정 (TimeFeatureBasedEntity에서 normally 자동 처리되지만, 수동으로 설정 필요)
		LocalDateTime now = LocalDateTime.of(2025, 1, 1, 10, 0);
		ReflectionTestUtils.setField(pet, "createdAt", now);

		List<String> imageUrls = List.of("https://example.com/image1.jpg", "https://example.com/image2.jpg");

		// when
		PetResponseDto dto = PetResponseDto.of(pet, imageUrls);

		// then
		assertThat(dto.getId()).isNull();  // id는 null → persist 안했기 때문
		assertThat(dto.getName()).isEqualTo("코코");
		assertThat(dto.getSize()).isEqualTo(PetSize.SMALL);
		assertThat(dto.getSpecies()).isEqualTo("푸들");
		assertThat(dto.getGender()).isEqualTo("암컷");
		assertThat(dto.getBirthday()).isEqualTo("2022-01-01");
		assertThat(dto.getImageUrls()).containsExactly("https://example.com/image1.jpg", "https://example.com/image2.jpg");
		assertThat(dto.getCreatedAt()).isEqualTo(now);
	}
}
