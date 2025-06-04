package com.study.petory.common.auth.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.study.petory.common.auth.service.CustomOAuth2UserService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity  // Spring Security 설정 활성화
@EnableMethodSecurity(prePostEnabled = true)  // @PreAuthorize, @PostAuthorize 활성화
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtFilter jwtFilter;
	private final CustomOAuth2UserService customOAuth2UserService;
	private final OAuth2SuccessHandler oAuth2SuccessHandler;

	/*
	1. .csrf : CSRF 설정 → JWT 기반이기 때문에 csrf 보호 비활성화
	2. .sessionManagement : 세션 관리 방식 설정
	3. .authorizeHttpRequests : URL 접근 권한 설정
			.requestMatchers : 해당 경로들은 로그인 없이 접근 허용 → permitAll(누구나 접근 허용)
			.anyRequest().authenticated() : 그 외의 모든 요청은 인증된 사용자만 접근 가능
	4. exceptionHandling : 인증 예외 처리 커스터마이징 → 인증 안 된 사용자가 보호된 리소스 접근 시 예외를 JSON 으로 반환
			.authenticationEntryPoint : 인증 안 된 사용자 접근 시 동작 지정
			response.setStatue : HTTP 상태 코드 401 UNAUTHORIZED 로 응답
			response.setContentType : 응답 형식을 JSON 으로 설정
			response.getWriter().write() : 사용자에게 JSON 메시지를 반환
	5. addFilterBefore : JWT 필터를 Security 필터 체인에 등록 → JWT 토큰 유효한지 먼저 검증 후 인증 처리 진행
	 */
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.csrf(AbstractHttpConfigurer::disable)
			.formLogin(AbstractHttpConfigurer::disable)
			.logout(AbstractHttpConfigurer::disable)
			.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(auth -> auth
				// TODO 수정 필요
				.requestMatchers(
					"/auth/**",
					"/users/signup",
					"/users/login",
					"/oauth2/**",       // 소셜 로그인 진입점 (예: /oauth2/authorization/google)
					"/login/oauth2/**", // 소셜 로그인 콜백 URI (예: /login/oauth2/code/google)
					"/error"
				).permitAll()
				.anyRequest().authenticated()
			)

			.exceptionHandling(ex -> ex
				.authenticationEntryPoint((request, response, authException) -> {
					response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
					response.setContentType("application/json;charset=UTF-8");
					response.getWriter().write("{\"status\":401,\"message\":\"인증이 필요합니다.\"}");
				})
			)

			.oauth2Login(oauth2 -> oauth2
				.userInfoEndpoint(userInfo -> userInfo
					.userService(customOAuth2UserService)  // OAuth2 사용자 정보 처리
				)
				.successHandler(oAuth2SuccessHandler)  // 로그인 성공 후 JWT 발급
			)

			.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	/*
	 * 비밀번호 암호화 하기 위해 Bean 등록
	 * 로그인 시 사용자 입력 비밀번호를 암호화된 비밀번호와 비교할 때 사용된다
	 * Security 는 내부적으로 이 메서드를 사용해 matches() 검사를 수행한다
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {

		return new BCryptPasswordEncoder();
	}
}

/* TODO
 * OAuth2SuccessHandler / OAuth2UserService 연동 완료 → 구현 필요
 */