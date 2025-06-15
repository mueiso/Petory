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

		// Security 전용 WHITELIST
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

		// 하위 경로 중 GET 매핑만 허용 (예: HttpMethod.GET, /places/**)
		this.permitGetPrefixList = List.of(
			"/owner-boards",
			"/places"
		);
	}
}
