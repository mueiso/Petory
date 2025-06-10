package com.study.petory.domain.user.service;

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

		// 1. 기본 OAuth2UserService 를 통해 OAuth2User 로딩
		OAuth2User oAuth2User = super.loadUser(userRequest);

		// 2. 사용자 정보에서 이메일 추출 (Google 은 기본 제공)
		String email = oAuth2User.getAttribute("email");

		// 3. 사용자 이름 또는 닉네임 추출
		String name = oAuth2User.getAttribute("name");

		// 4. 이메일이 없으면 예외 발생 → 회원 식별이 불가하므로
		if (email == null || email.isBlank()) {
			throw new IllegalArgumentException("이메일 정보가 제공되지 않았습니다.");
		}

		// 5. 사용자 정보가 DB에 존재하지 않으면 새로 생성
		User user = userRepository.findByEmail(email).orElseGet(() -> {

			// 5-1. 개인 정보 객체 생성
			UserPrivateInfo privateInfo = UserPrivateInfo.builder()
				.authId(userRequest.getClientRegistration().getRegistrationId())  // 예: "google"
				.name(name)
				.mobileNum("")  // 초기값 설정 (필요시 나중에 업데이트)
				.build();

			// 5-2. 기본 사용자 역할 설정 (예: ROLE_USER)
			UserRole userRole = UserRole.builder()
				.role(Role.USER)
				.build();

			// 5-3. 사용자 객체 생성 및 저장
			return userRepository.save(User.builder()
				.nickname(name)  // 초기 닉네임 설정
				.email(email)
				.userPrivateInfo(privateInfo)
				.userRole(List.of(userRole))  // 역할 연결
				.build());
		});

		// 6. 사용자 권한을 Spring Security 권한 객체로 매핑
		List<SimpleGrantedAuthority> authorities = user.getUserRole().stream()
			.map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRole().name()))
			.toList();

		// 7. DefaultOAuth2User 객체 생성하여 반환 (SecurityContext 에 저장될 사용자 정보)
		return new DefaultOAuth2User(
			authorities,                      // 사용자 권한
			oAuth2User.getAttributes(),       // OAuth2 프로바이더에서 받은 전체 사용자 정보
			"email"                           // 사용자 식별 키 (principal)
		);
	}
}
