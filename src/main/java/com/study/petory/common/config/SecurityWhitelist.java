package com.study.petory.common.config;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.Getter;

@Getter
@Component
public class SecurityWhitelist {

	private final List<String> urlWhitelist;
	private final List<String> permitGetPrefixList;

	public SecurityWhitelist() {

		this.urlWhitelist = List.of(
			"/auth/reissue",
			"/auth/logout",
			"/users/test-login",
			"/login.html",
			"/login-success.html",
			"/favicon.ico",
			"/map.html",
			"/trade-boards",
			"/trade-boards/{tradeBoardId}",
			"/questions/today"
		);

		this.permitGetPrefixList = List.of(
			"/owner-boards",
			"/places"
		);
	}
}
