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
			"/favicon.ico",
			"/ws-chat",
			"/ws-chat/**",
			"/test/**",
			"/image-petory.png",
			"/petory.ico",
			"/actuator/prometheus",
			"/place-search-pet.png",

			"/",
			"/login",
			"/petPlace",
			"/community",
			"/market",
			"/myCalendar",
			"/login.html",
			"/index.html",
			"/community.html",
			"/community-new.html",
			"/chat.html",
			"/market.html",
			"/market-detail.html",
			"/map.html",
			"/calendar.html",

			"/images/**",
			"/static/**",          // 정적 리소스들 (HTML, CSS, JS, 이미지 등 모두 포함)

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
