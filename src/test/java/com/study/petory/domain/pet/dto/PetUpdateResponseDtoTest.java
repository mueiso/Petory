package com.study.petory.domain.pet.dto;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.study.petory.domain.pet.entity.Pet;
import com.study.petory.domain.pet.entity.PetSize;
import com.study.petory.domain.user.entity.User;

class PetUpdateResponseDtoTest {

	@Test
	void of_정상적으로_DTO_생성됨() {

		// given - 테스트용 사용자 생성
		User user = new User(1L);  // ID만 설정된 간단한 User 객체

		// 테스트용 반려동물 생성
		Pet pet = Pet.builder()
			.name("코코")
			.size(PetSize.MEDIUM)
			.species("말티즈")
			.gender("암컷")
			.birthday("2023-03-15")
			.user(user)
			.build();

		// 이미지 URL 리스트
		List<String> imageUrls = List.of(
			"https://s3.aws.com/pet1.jpg",
			"https://s3.aws.com/pet2.jpg"
		);

		// when - DTO 변환
		PetUpdateResponseDto dto = PetUpdateResponseDto.of(pet, imageUrls);

		// then - DTO 필드 값 검증
		assertThat(dto.getId()).isNull();  // 저장 전이라 ID는 null
		assertThat(dto.getName()).isEqualTo("코코");
		assertThat(dto.getSize()).isEqualTo(PetSize.MEDIUM);
		assertThat(dto.getSpecies()).isEqualTo("말티즈");
		assertThat(dto.getGender()).isEqualTo("암컷");
		assertThat(dto.getBirthday()).isEqualTo("2023-03-15");
		assertThat(dto.getImageUrls()).containsExactlyElementsOf(imageUrls);
	}
}
