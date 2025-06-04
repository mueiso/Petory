package com.study.petory.common.auth.service;

import java.util.Collections;

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
		OAuth2User oAuth2User = super.loadUser(userRequest);

		String email = oAuth2User.getAttribute("email");
		String name = oAuth2User.getAttribute("name");

		// 이미 가입된 사용자인지 확인
		User user = userRepository.findByEmail(email).orElseGet(() -> {
			// 기본 사용자 개인 정보 생성 (필요시 수정)
			UserPrivateInfo privateInfo = UserPrivateInfo.builder()
				.authId(userRequest.getClientRegistration().getRegistrationId())
				.name(name)
				.mobileNum("")  // 초기값 설정
				.build();

			// 기본 역할 부여
			UserRole role = UserRole.builder()
				.role(Role.USER)
				.build();

			return userRepository.save(User.builder()
				.nickname(name)
				.email(email)
				.userPrivateInfo(privateInfo)
				.userRole(Collections.singletonList(role))
				.build());
		});

		// JWT 발급용으로 필요한 사용자 정보 반환
		return new DefaultOAuth2User(
			Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
			oAuth2User.getAttributes(),
			"email"
		);
	}
}
