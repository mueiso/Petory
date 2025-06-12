package com.study.petory.common.security;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;

public class CustomPrincipal {
	private final Long   id;
	private final String email;
	private final String nickname;
	private final Collection<? extends GrantedAuthority> authorities;

	public CustomPrincipal(
		Long id,
		String email,
		String nickname,
		Collection<? extends GrantedAuthority> authorities
	) {
		this.id          = id;
		this.email       = email;
		this.nickname    = nickname;
		this.authorities = authorities;
	}

	public Long getId()           { return id; }
	public String getEmail()      { return email; }
	public String getNickname()   { return nickname; }
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}
}
