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

		/* [given]
		 * 테스트용 사용자 생성 (ID만 설정된 간단한 User 객체)
		 */
		User user = new User(1L);

		// 테스트용 반려동물 생성
		Pet pet = Pet.builder()
			.name("쿠키")
			.size(PetSize.MEDIUM)
			.species("푸들")
			.gender("수컷")
			.birthday("2023-03-15")
			.user(user)
			.build();

		// 이미지 URL 리스트 (S3에서 제공된 링크)
		List<String> imageUrls = List.of(
			"https://s3.aws.com/pet1.jpg",
			"https://s3.aws.com/pet2.jpg"
		);

		/* [when]
		 * Pet 객체를 기반으로 DTO 생성
		 */
		PetUpdateResponseDto dto = PetUpdateResponseDto.of(pet, imageUrls);

		/* [then]
		 * DTO 의 각 필드가 정상적으로 매핑되었는지 검증
		 * ID는 아직 저장 전이므로 null
		 * 이미지 리스트도 일치 여부 확인
		 */
		assertThat(dto.getId()).isNull();
		assertThat(dto.getName()).isEqualTo("쿠키");
		assertThat(dto.getSize()).isEqualTo(PetSize.MEDIUM);
		assertThat(dto.getSpecies()).isEqualTo("푸들");
		assertThat(dto.getGender()).isEqualTo("수컷");
		assertThat(dto.getBirthday()).isEqualTo("2023-03-15");
		assertThat(dto.getImageUrls()).containsExactlyElementsOf(imageUrls);
	}
}
