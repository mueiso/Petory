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
			"/users/test-login",
			"/login.html",
			"/login-success.html",
			"/favicon.ico",
			"/map.html",
			"/chat.html",
			"/ws-chat",
			"/ws-chat/**"
			// 캘린더 접근 권한 요청 프론트 필요
			// 캘린더 프론트
		);

		// GET 매핑만 허용 (예: HttpMethod.GET, /places)
		this.permitGetPrefixList = List.of(
			"/owner-boards/**",
			"/places",
			"/places/{placeId}",
			"/trade-boards/**",
			"/albums/all",
			"/albums/all/users/{userId}",
			"/albums/{albumId}",
			"/questions/today"
		);
	}
}
