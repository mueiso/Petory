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

		/* [given]
		 * 최소한의 유저 객체 (ID만 설정)
		 */
		User user = new User(1L);

		// 테스트용 Pet 객체 생성
		Pet pet = Pet.builder()
			.name("쿠키")
			.size(PetSize.SMALL)
			.species("푸들")
			.gender("수컷")
			.birthday("2022-01-01")
			.user(user)
			.build();

		/*
		 * createdAt 은 TimeFeatureBasedEntity 상속으로 자동 생성되지만 테스트에서는 수동으로 설정 필요
		 * ReflectionTestUtils 이용해 강제로 필드값 주입
		 * 반려동물 이미지 url 리스트 설정
		 */
		LocalDateTime now = LocalDateTime.of(2025, 1, 1, 10, 0);
		ReflectionTestUtils.setField(pet, "createdAt", now);
		List<String> imageUrls = List.of("https://example.com/image1.jpg", "https://example.com/image2.jpg");

		/* [when]
		 * DTO 변환 메서드 호출
		 */
		PetResponseDto dto = PetResponseDto.of(pet, imageUrls);

		/* [then]
		 * (변환된 DTO 값 검증)
		 * id 는 persist 안했기 때문에 DB 저장 전이라 null
		 * 이름 확인
		 * 사이즈 확인
		 * 종 확인
		 * 성별 확인
		 * 생일 확인
		 * 이미지 url 리스트 확인
		 * 생성일 확인
		 */
		assertThat(dto.getId()).isNull();
		assertThat(dto.getName()).isEqualTo("쿠키");
		assertThat(dto.getSize()).isEqualTo(PetSize.SMALL);
		assertThat(dto.getSpecies()).isEqualTo("푸들");
		assertThat(dto.getGender()).isEqualTo("수컷");
		assertThat(dto.getBirthday()).isEqualTo("2022-01-01");
		assertThat(dto.getImageUrls()).containsExactly("https://example.com/image1.jpg", "https://example.com/image2.jpg");
		assertThat(dto.getCreatedAt()).isEqualTo(now);
	}
}
