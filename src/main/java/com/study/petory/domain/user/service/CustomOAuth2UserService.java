package com.study.petory.domain.user.service;

import java.util.Collections;
import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.study.petory.domain.user.entity.Role;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.entity.UserPrivateInfo;
import com.study.petory.domain.user.entity.UserRole;
import com.study.petory.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

	private final UserRepository userRepository;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) {

		// 기본 OAuth2UserService 를 사용해 사용자 정보 불러오기
		OAuth2User oAuth2User = super.loadUser(userRequest);

		// 구글에서 받은 사용자 정보 추출
		String email = oAuth2User.getAttribute("email");
		String name = oAuth2User.getAttribute("nickname");

		// 이메일 값이 없으면 예외 발생
		if (email == null || email.isBlank()) {
			throw new IllegalArgumentException("이메일 정보가 제공되지 않았습니다.");
		}

		// 사용자 조회 또는 신규 생성
		User user = userRepository.findByEmail(email).orElseGet(() -> {

			// 1. 개인 정보 객체 생성
			UserPrivateInfo privateInfo = UserPrivateInfo.builder()
				.authId(userRequest.getClientRegistration().getRegistrationId())
				.name(name)
				.mobileNum("")  // 초기값 설정
				.build();

			// 2. 사용자 역할 설정 (기본 USER)
			UserRole userRole  = UserRole.builder()
				.role(Role.USER)
				.build();

			// 유저와 역할 매핑
			List<UserRole> userRoles = List.of(userRole);

			// User 엔티티 생성 및 저장
			return userRepository.save(User.builder()
				.nickname(name)  // 초기 닉네임은 이름으로 설정
				.email(email)
				.userPrivateInfo(privateInfo)
				.userRole(userRoles)
				.build());
		});

		// DB에 저장된 역할로부터 권한 동기화
		List<SimpleGrantedAuthority> authorities = user.getUserRole().stream()
			.map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRole().name()))
			.toList();

		// OAuth2User 반환 (SecurityContext 에 등록될 사용자 객체)
		return new DefaultOAuth2User(
			authorities,
			oAuth2User.getAttributes(),  // 사용자 속성 전체
			"email"  // principal key: SecurityContext 에서 사용자 식별
		);
	}
}
