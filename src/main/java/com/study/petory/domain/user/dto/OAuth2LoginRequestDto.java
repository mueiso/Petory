package com.study.petory.domain.user.dto;

import java.util.Collections;

import com.study.petory.domain.user.entity.User;

import jakarta.validation.constraints.NotBlank;
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
	private String password;
}
