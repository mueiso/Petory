package com.study.petory.domain.pet.entity;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.study.petory.domain.user.entity.User;

class PetImageTest {

	@Test
	void 생성자_호출_정상생성_및_연관관계설정() {

		// [given]
		Pet pet = Pet.builder()
			.name("쿠키")
			.size(PetSize.SMALL)
			.species("푸들")
			.gender("수컷")
			.birthday("2023-03-01")
			.user(new User(1L))
			.build();

		String imageUrl = "https://s3.aws.com/image1.jpg";

		// [when]
		PetImage petImage = new PetImage(imageUrl, pet);

		/* [then]
		 * URL 정상 저장 확인
		 * 연관된 Pet 객체가 정확한지 확인
		 */
		assertThat(petImage.getUrl()).isEqualTo(imageUrl);
		assertThat(petImage.getPet()).isEqualTo(pet);
	}
}
