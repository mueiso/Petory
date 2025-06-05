package com.study.petory.common.auth.security;

import java.io.IOException;
import java.util.List;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import com.study.petory.common.auth.service.JwtProvider;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
// OncePerRequestFilter 를 상속받아 HTTP 요청당 한 번만 실행
public class JwtFilter extends OncePerRequestFilter {

	private final JwtProvider jwtProvider;

	private final RedisTemplate<String, String> redisTemplate;

	// TODO - URL 추가 및 수정 필요
	// WHITELIST
	private static final List<String> WHITELIST = List.of(
		"/auth/login",
		"/users/signup",
		"/auth/refresh"
	);

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		// HttpServletRequest 에서 요청된 URI 경로를 문자열로 추출
		String url = request.getRequestURI();
		debugLog("요청 URI: " + url);

		// 요청된 URL 이 인증 우회 대상인지 판단 후 JWT 인증 필터 건너뛰기/ 후 다음 필터로 넘김
		if (WHITELIST.contains(url)) {
			debugLog("WHITELIST 경로입니다. 필터 우회: " + url);
			filterChain.doFilter(request, response);
			return;
		}

		// 클라이언트가 HTTP 요청에 Authorization 헤더 포함시켰는지 확인
		String bearerJwt = request.getHeader("Authorization");
		debugLog("Authorization 헤더: " + bearerJwt);

		if (bearerJwt == null) {
			debugLog("Authorization 헤더 없음. 필터 중단");
			writeErrorResponse(response, HttpStatus.UNAUTHORIZED, "Authorization 헤더가 존재하지 않습니다.");
			return;
		}

		/*
		 * 토큰 파싱 및 유효성 검사
		 * subStringToken() : 유효성 검사 + Bearer 제거
		 * subStringToken() 안에서 발생한 예외 잡아서 HTTP 응답에 401 에러와 함께 메시지 JSON 으로 응답
		 */
		String jwt;

		try {
			jwt = jwtProvider.subStringToken(bearerJwt);
			debugLog("추출된 JWT: " + jwt);
		} catch (ResponseStatusException e) {
			debugLog("JWT 파싱 실패: " + e.getReason());
			writeErrorResponse(response, e.getStatusCode(), e.getReason());
			return;
		}

		/*
		 * tokenKey : Redis 에 저장된 블랙리스트 토큰의 Key 를 구성하는 부분
		 * jwt 는 현재 요청에서 추출한 Access Token 문자열이기 때문에
		   → Redis 에 저장할 때 "BLACKLIST_" 접두어 붙여서 구분
		 */
		String tokenKey = "BLACKLIST_" + jwt;

		/*
		 * Redis 에서 tokenKey 존재하는지 확인
		 * hasKey() : 해당 키가 Redis 에 존재하는지 여부에 따라 true/false 반환
		 */
		Boolean isBlackListed = redisTemplate.hasKey(tokenKey);
		debugLog("Redis 블랙리스트 키 조회: " + tokenKey + " / 존재 여부: " + isBlackListed);

		/*
		 * isBlackListed 가 true 인지 확인 → null-safe 체크
		 * Redis 에서 해당 토큰이 블랙리스트에 등록되어 있으면 writeErrorResponse 로직으로 진입
		 */
		if (Boolean.TRUE.equals(isBlackListed)) {
			debugLog("블랙리스트 토큰 발견. 필터 중단");
			writeErrorResponse(response, HttpStatus.UNAUTHORIZED, "로그아웃된 토큰입니다. 다시 로그인 해주세요.");
			return;
		}

		try {
			// 내부에서 Bearer 제거 + 토큰의 유효성 검증 → Claims 객체로 반환
			Claims claims = jwtProvider.getClaims(bearerJwt);
			// Claims 는 JWT 내부 payload 정보들을 갖고 있어 getSubject() 로 값 추출 가능
			String userId = claims.getSubject();
			debugLog("JWT Claims 파싱 성공 - 사용자 ID: " + userId);

			// TODO - 권한 부여 로직 추가 후 권한 목록 수정 → List.of()
			// Spring Security 에서 사용하는 인증 객체 생성
			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userId, null,
				List.of());

			/*
			 * 보안 컨텍스트에 방금 만든 authentication 객체를 저장
			   → 이후 컨트롤러 단에서 @AuthenticationPrincipal 같은 방식으로 사용자 정보를 꺼낼 수 있다
			   → 인증을 수동으로 완료 처리함
			 */
			SecurityContextHolder.getContext().setAuthentication(authentication);
			debugLog("SecurityContext 등록 완료 - 인증 사용자 ID: " + userId);

			debugLog("JWT 인증 완료. 다음 필터로 전달");
			filterChain.doFilter(request, response);
			// jwtProvider.getClaims() 에서 토큰 만료됐거나 위조된 경우 예외 발생
		} catch (ResponseStatusException e) {
			debugLog("JWT 검증 실패 - 이유: " + e.getReason());
			writeErrorResponse(response, e.getStatusCode(), e.getReason());
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

/* TODO
 * 로그아웃 시 AccessToken 을 Redis 에 저장 (블랙리스트) 로직 추가
 * UserDetailsService 도 커스터마이징
 */