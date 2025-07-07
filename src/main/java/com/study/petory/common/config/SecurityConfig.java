package com.study.petory.common.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.study.petory.common.ratelimit.RateLimitFilter;
import com.study.petory.common.security.CustomAccessDeniedHandler;
import com.study.petory.common.security.JwtAuthenticationEntryPoint;
import com.study.petory.common.security.JwtFilter;
import com.study.petory.domain.user.service.CustomOAuth2UserService;
import com.study.petory.domain.user.service.OAuth2FailureHandler;
import com.study.petory.domain.user.service.OAuth2SuccessHandler;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtFilter jwtFilter;
	private final CustomOAuth2UserService customOAuth2UserService;
	private final OAuth2SuccessHandler oAuth2SuccessHandler;
	private final OAuth2FailureHandler oAuth2FailureHandler;
	private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
	private final CustomAccessDeniedHandler customAccessDeniedHandler;
	private final SecurityWhitelist securityWhitelist;

	@Value("${app.cors.allowed-origins}")
	private String allowedOriginsString;

	@Value("${app.cors.allow-credentials}")
	private boolean allowCredentials;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http, RateLimitFilter rateLimitFilter) throws Exception {
		http
			// CORS 설정 적용
			.cors(cors -> cors.configurationSource(corsConfigurationSource()))
			.csrf(AbstractHttpConfigurer::disable)
			.formLogin(AbstractHttpConfigurer::disable)
			.logout(AbstractHttpConfigurer::disable)
			.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(auth -> auth
				// Security 전용 WHITELIST
				.requestMatchers(securityWhitelist.getUrlWhitelist().toArray(new String[0]))
				.permitAll()
				// GET 메서드의 특정 경로 한정 허용
				.requestMatchers(HttpMethod.GET, securityWhitelist.getPermitGetPrefixList().toArray(new String[0]))
				.permitAll()
				.anyRequest()
				.authenticated()
			)

			.exceptionHandling(ex -> ex
				.authenticationEntryPoint(jwtAuthenticationEntryPoint)
				.accessDeniedHandler(customAccessDeniedHandler)
			)

			.oauth2Login(oauth2 -> oauth2
				.userInfoEndpoint(userInfo -> userInfo
					.userService(customOAuth2UserService)
				)
				.successHandler(oAuth2SuccessHandler)
				.failureHandler(oAuth2FailureHandler)
			)

			.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
			.addFilterAfter(rateLimitFilter, JwtFilter.class); // rateLimitFilter는 jwtFilter 이후에 실행

		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {

		return new BCryptPasswordEncoder();
	}

	/*
	 * CORS 설정 위한 Bean
	 * React 등 다른 도메인에서 요청 시 Access-Control-Allow-Credentials, Access-Control-Allow-Origin 등을 정상 처리
	 */
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {

		// CORS 설정 객체 생성
		CorsConfiguration config = new CorsConfiguration();

		List<String> allowedOrigins = List.of(allowedOriginsString.split(","));

		// React 등 프론트엔드 개발 서버
		config.setAllowedOrigins(allowedOrigins);

		// 허용할 HTTP 메서드 지정
		config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

		// 허용할 요청 헤더 지정
		config.setAllowedHeaders(List.of("Authorization", "Content-Type"));

		// expose 설정 추가 (프론트가 응답 헤더 읽을 수 있도록)
		config.setExposedHeaders(List.of("Authorization", "X-Refresh-Token"));

		// 자격 증명 포함 허용 (예: 쿠키, Authorization 헤더 등)
		config.setAllowCredentials(allowCredentials);

		// CORS 설정을 특정 경로 패턴에 매핑
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

		// 모든 요청 경로(/**)에 대해 CORS 설정 적용
		source.registerCorsConfiguration("/**", config);

		return source;
	}
}
