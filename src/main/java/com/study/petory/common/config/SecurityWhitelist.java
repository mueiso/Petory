package com.study.petory.common.config;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.Getter;

@Getter
@Component
public class SecurityWhitelist {

	private final List<String> urlWhitelist;
	private final List<String> PermittedGETPrefixList;

	public SecurityWhitelist() {

		this.urlWhitelist = List.of(
			"/auth/reissue",
			"/auth/logout",
			"/users/test-login",
			"/login.html",
			"/favicon.ico",
			"/map.html",
			"/trade-boards",
			"/trade-boards/{tradeBoardId}",
			"/questions/today"
		);

		this.PermittedGETPrefixList = List.of(
			"/owner-boards",
			"/places"
		);
	}
}
