package com.study.petory.common.config;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.Getter;

@Getter
@Component
public class SecurityWhitelist {

	private final List<String> urlWhitelist;
	private final List<String> permitGetPrefixList;

	// TODO - 배포 전 확인 필요
	public SecurityWhitelist() {

		// Security 전용 WHITELIST
		this.urlWhitelist = List.of(
			"/auth/reissue",
			"/users/test-login",
			"/login.html",
			"/login-success.html",
			"/favicon.ico",
			"/map.html",
			"/chat.html",
			"/ws-chat",
			"/ws-chat/**",
			"/test/send-email"
		);

		// GET 매핑만 허용 (예: HttpMethod.GET, /places)
		this.permitGetPrefixList = List.of(
			"/owner-boards/**",
			"/places",
			"/places/{placeId}",
			"/trade-boards",
			"/trade-boards/{tradeBoardId}",
			"/albums/all",
			"/albums/all/users/{userId}",
			"/albums/{albumId}",
			"/questions/today"
		);
	}
}
