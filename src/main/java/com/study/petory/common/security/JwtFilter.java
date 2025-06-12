package com.study.petory.common.security;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.study.petory.common.exception.CustomException;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

// TODO - ResponseStatusException 대신 CustomException 으로 통일
@Slf4j
@Component
// OncePerRequestFilter 를 상속받아 HTTP 요청당 한 번만 실행
public class JwtFilter extends OncePerRequestFilter {

	private final JwtProvider jwtProvider;

	private final RedisTemplate<String, String> loginRefreshToken;

	public JwtFilter(
		JwtProvider jwtProvider,
		@Qualifier("loginRefreshToken")
		RedisTemplate<String, String> loginRefreshToken
	) {
		this.jwtProvider = jwtProvider;
		this.loginRefreshToken = loginRefreshToken;
	}

	// TODO - URL 추가 및 수정 필요
	// WHITELIST
	private static final List<String> WHITELIST = List.of(
		"/auth/login",
		"/users/signup",
		"/auth/refresh",
		"/login.html",
		"/favicon.ico"
	);

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		// HttpServletRequest 에서 요청된 URI 경로를 문자열로 추출
		String url = request.getRequestURI();
		debugLog("요청 URI: " + url);

		// WHITELIST or 정적 리소스는 필터 우회
		if (url.matches(".*(\\.html|\\.css|\\.js|\\.png|\\.jpg|\\.ico)$") || WHITELIST.contains(url)) {
			debugLog("WHITELIST 경로입니다. 필터 우회: " + url);
			filterChain.doFilter(request, response);

			return;
		}

		// 1. 클라이언트가 HTTP 요청에 Authorization 헤더 포함시켰는지 확인
		String bearerJwt = request.getHeader("Authorization");

		// 쿼리 파라미터에서도 accessToken 시도 (테스트 목적)
		if (bearerJwt == null) {
			String queryToken = request.getParameter("accessToken");
			if (queryToken.startsWith("Bearer ")) {
				bearerJwt = queryToken;
			} else {
				bearerJwt = "Bearer " + queryToken;
			}
			debugLog("accessToken 쿼리 파라미터 사용: " + bearerJwt);
		}

		if (bearerJwt == null) {
			debugLog("Authorization 헤더 없음. 필터 중단");
			writeErrorResponse(response, HttpStatus.UNAUTHORIZED, "Authorization 헤더가 존재하지 않습니다.");
			return;
		}

		// 2) "Bearer " 제거 + 유효성 검사 : subStringToken()
		String rawToken;

		try {
			rawToken = jwtProvider.subStringToken(bearerJwt);
			debugLog("추출된 JWT: " + rawToken);
		} catch (CustomException e) {
			writeErrorResponse(response, e.getErrorCode().getStatus(), e.getMessage());

			return;
		}

		// 3) 블랙리스트 체크
		if (Boolean.TRUE.equals(loginRefreshToken.hasKey("BLACKLIST_" + rawToken))) {
			writeErrorResponse(response, HttpStatus.UNAUTHORIZED, "로그아웃된 토큰입니다.");
			return;
		}

		try {
			/*
			 * 4) 토큰 파싱
			 * 내부에서 토큰의 유효성 검증 → Claims 객체로 반환
			 */
			Claims claims = jwtProvider.parseRawToken(rawToken);

			// Claims 는 JWT 내부 payload 정보들을 갖고 있어 getSubject() 로 값 추출 가능
			Long userId = Long.valueOf(claims.getSubject());
			String email = claims.get("email", String.class);
			String nickname = claims.get("nickname", String.class);
			debugLog("JWT Claims 파싱 성공 - userId: " + userId);

			// 5) 권한 매핑 (예시: ROLE_USER)
			var authorities  = List.of(new SimpleGrantedAuthority("ROLE_USER"));

			// 6)  UserDetails 대신 CustomPrincipal 생성
			CustomPrincipal principal =
				new CustomPrincipal(userId, email, nickname, authorities);

			// 7) Authentication 객체 생성 & SecurityContext 에 저장
			var authentication =
				new UsernamePasswordAuthenticationToken(principal, null, authorities);

			/*
			 * 보안 컨텍스트에 방금 만든 authentication 객체를 저장
			   → 이후 컨트롤러 단에서 @AuthenticationPrincipal 같은 방식으로 사용자 정보를 꺼낼 수 있다
			   → 인증을 수동으로 완료 처리함
			 */
			SecurityContextHolder.getContext().setAuthentication(authentication);
			debugLog("SecurityContext 등록 완료 - principal: " + principal.getEmail());

			debugLog("JWT 인증 완료. 다음 필터로 전달");
			filterChain.doFilter(request, response);
			// jwtProvider.getClaims() 에서 토큰 만료됐거나 위조된 경우 예외 발생
		} catch (CustomException e) {
			debugLog("JWT 검증 실패 - 이유: " + e.getMessage());
			writeErrorResponse(response, e.getErrorCode().getStatus(), e.getMessage());
		}
	}

	// JWT 없거나 잘못된 경우 사용할 공통 메서드
	private void writeErrorResponse(HttpServletResponse response, HttpStatusCode status, String message) throws
		IOException {

		response.setStatus(status.value());
		response.setContentType("application/json;charset=UTF-8");
		response.getWriter().write(String.format("{\"status\":%d,\"message\":\"%s\"}", status.value(), message));
	}

	// 커스텀 디버깅 메시지용 메서드
	private void debugLog(String message) {
		log.debug("[JwtFilter] {}", message);
	}
}
