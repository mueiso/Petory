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
			"/favicon.ico",
			"/map.html",
			"/chat.html",
			"/ws-chat",
			"/ws-chat/**",
			"/test/**",
			"/image-petory.png",
			"/petory.ico",
			"/actuator/prometheus",
			"/place-search-pet.png",
			"/calendar.html",
			"/actuator/health"
		);

		// GET 매핑만 허용 (예: HttpMethod.GET, /places)
		this.permitGetPrefixList = List.of(
			"/owner-boards/**",
			"/places",
			"/places/{placeId}",
			"/trade-boards",
			"/trade-boards/{tradeBoardId}",
			"/users/albums",
			"/users/{userId}/albums",
			"/users/albums/{albumId}",
			"/daily-questions/today",
			"/places/rank"
		);
	}
}
