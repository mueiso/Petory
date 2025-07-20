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
@EnableWebSecurity  // Spring Security 설정 활성화
@EnableMethodSecurity(prePostEnabled = true)  // @PreAuthorize, @PostAuthorize 활성화
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

	/*
	 * 1. .csrf : CSRF 설정 → JWT 기반이기 때문에 csrf 보호 비활성화
	 * 2. .sessionManagement : 세션 관리 방식 설정
	 * 3. .authorizeHttpRequests : URL 접근 권한 설정
			.requestMatchers : 해당 경로들은 로그인 없이 접근 허용 → permitAll(누구나 접근 허용)
			.anyRequest().authenticated() : 그 외의 모든 요청은 인증된 사용자만 접근 가능
	 * 4. exceptionHandling : 인증 예외 처리 커스터마이징 → 인증 안 된 사용자가 보호된 리소스 접근 시 예외를 JSON 으로 반환
			.authenticationEntryPoint : 인증 안 된 사용자 접근 시 동작 지정
	 * 5. addFilterBefore : JWT 필터를 Security 필터 체인에 등록 → JWT 토큰 유효한지 먼저 검증 후 인증 처리 진행
	 */
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
					.userService(customOAuth2UserService)  // OAuth2 사용자 정보 처리
				)
				.successHandler(oAuth2SuccessHandler)  // 로그인 성공 후 JWT 발급
				.failureHandler(oAuth2FailureHandler)  // 로그인 실패 시 처리
			)

			.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
			.addFilterAfter(rateLimitFilter, JwtFilter.class); // rateLimitFilter는 jwtFilter 이후에 실행

		return http.build();
	}

	/*
	 * 비밀번호 암호화용 Bean
	 * 로그인 시 사용자 입력 비밀번호를 암호화된 비밀번호와 비교할 때 사용된다
	 * Security 는 내부적으로 이 메서드를 사용해 matches() 검사를 수행한다
	 */
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

		/*
		 * 허용할 프론트 주소 (클라이언트 도메인 지정)
		 * 어떤 프론트엔드 클라이언트가 백엔드 API 에 접근할 수 있는지를 명시하는 설정
		 * http://localhost:3000 : 개발 중 로컬에서 프론트엔드를 실행할 때 API 요청을 허용하려고 사용
		 * https://www.petory.click : 운영 환경의 실제 배포된 프론트엔드 도메인, 사용자들이 웹사이트를 통해 백엔드 API 에 접근할 때 사용
		 */
		config.setAllowedOrigins(allowedOrigins);  // React 등 프론트엔드 개발 서버

		// 허용할 HTTP 메서드 지정
		config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

		// 허용할 요청 헤더 지정
		config.setAllowedHeaders(List.of("Authorization", "Content-Type"));

		// expose 설정 추가 (프론트가 응답 헤더 읽을 수 있도록)
		config.setExposedHeaders(List.of("Authorization", "X-Refresh-Token"));

		/*
		 * 프론트 구현하여 연동 시 true 로 설정 변경 필요
		 * 자격 증명 포함 허용 (예: 쿠키, Authorization 헤더 등)
		 */
		config.setAllowCredentials(allowCredentials);

		// 프론트 연동 시 필요
		// config.setExposedHeaders(List.of("Authorization"));

		// CORS 설정을 특정 경로 패턴에 매핑
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

		// 모든 요청 경로(/**)에 대해 CORS 설정 적용
		source.registerCorsConfiguration("/**", config);

		return source;
	}
}
