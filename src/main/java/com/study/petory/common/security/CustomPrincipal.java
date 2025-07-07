package com.study.petory.common.security;

import java.security.Principal;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

import lombok.Getter;

@Getter
public class CustomPrincipal implements Principal {
	private final Long id;
	private final String email;
	private final String nickname;
	private final Collection<? extends GrantedAuthority> authorities;

	public CustomPrincipal(
		Long id,
		String email,
		String nickname,
		Collection<? extends GrantedAuthority> authorities
	) {
		this.id = id;
		this.email = email;
		this.nickname = nickname;
		this.authorities = authorities;
	}

	@Override
	public String getName() {
		return String.valueOf(this.id);
	}
}
