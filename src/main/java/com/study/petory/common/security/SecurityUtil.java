package com.study.petory.common.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class SecurityUtil {

	// 현재 사용자가 특정 권한(ROLE_*)을 가지고 있는지 검사
	public static boolean hasRole(String role) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || auth.getAuthorities() == null) {
			return false;
		}
		return auth.getAuthorities().contains(new SimpleGrantedAuthority(role));
	}
}
