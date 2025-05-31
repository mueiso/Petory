package com.study.petory.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity  // Spring Security 설정 활성화
@EnableMethodSecurity(prePostEnabled = true)  // @PreAuthorize, @PostAuthorize 활성화
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtFilter jwtFilter;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		// CSRF 설정 → JWT 기반이기 때문에 csrf 보호 비활성화
		http.csrf(AbstractHttpConfigurer::disable)
			// 세션 관리 방식 설정
			.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			// URL 접근 권한 설정
			.authorizeHttpRequests(auth -> auth
				// 해당 경로들은 로그인 없이 접근 허용 → permitAll(누구나 접근 허용)
				.requestMatchers("/auth/**", "/users/signup", "/users/login", "/error", "/oauth2/**").permitAll()
				// 그 외의 모든 요청은 인등된 사용자만 접근 가능
				.anyRequest().authenticated()
			)
			// 인증 예외 처리 커스터마이징 → 인증 X 사용자사 보호된 리소스 접근 시 예외를 JSON 으로 반환
			.exceptionHandling(ex -> ex
				// 인증 안 된 사용자 접근 시 동작 지정
				.authenticationEntryPoint((request, response, authException) -> {
					// HTTP 산태코드 401 UNAUTHORIZED 로 응답
					response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
					// 응답 형식을 JSON 으로 설정
					response.setContentType("application/json;charset=UTF-8");
					// 사용자에게 JSON 메시지를 반환
					response.getWriter().write("{\"status\":401,\"message\":\"인증이 필요합니다.\"}");
				})
			)
			// JWT 필터를 Security 필터 체인에 등록 → JWT 토큰 유효한지 먼저 검증 후 인증 처리 진행
			.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

}
