package com.study.petory.common.auth.dto;

import com.study.petory.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OAuth2LoginRequestDto {
	private String email;
	private String nickname;

	public User toUser() {
		return User.builder()
			.email(this.email)
			.nickname(this.nickname)
			.build();
	}
}
