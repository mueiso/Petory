package com.study.petory.common.security;

import java.io.IOException;
import java.util.List;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.study.petory.common.config.SecurityWhitelist;
import com.study.petory.common.exception.CustomException;
import com.study.petory.common.exception.enums.ErrorCode;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/*
 * OncePerRequestFilter 를 상속받아 HTTP 요청당 한 번만 실행
 * JWT 인증 필터 - HTTP 요청마다 실행
 * 인증 필요 없는 경로는 WHITELIST 로 필터링하고, 나머지는 JWT 검증 처리
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

	private static final String HEADER_AUTHORIZATION = "Authorization";
	private static final String TOKEN_PREFIX = "Bearer ";

	private final JwtProvider jwtProvider;
	private final SecurityWhitelist securityWhitelist;
	private final RedisTemplate<String, String> loginRefreshToken;
	private final AntPathMatcher pathMatcher = new AntPathMatcher();

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		// 요청 URI
		String url = request.getRequestURI();
		String method = request.getMethod();
		String servletPath = request.getServletPath();
		String contextPath = request.getContextPath();
		String fullUrl = request.getRequestURL().toString();

		debugLog("요청 로그 확인 --------------------------");
		debugLog("Method: " + method);
		debugLog("Request URI: " + url);
		debugLog("Servlet Path: " + servletPath);
		debugLog("Context Path: " + contextPath);
		debugLog("전체 URL: " + fullUrl);

		// 화이트리스트 경로 우회
		for (String pattern : securityWhitelist.getUrlWhitelist()) {

			if (pathMatcher.match(pattern, url)) {
				debugLog("WHITELIST 경로입니다. 필터 우회: " + url);
				filterChain.doFilter(request, response);
				return;
			}
		}

		// GET 매핑의 /owner-boards 하위 경로 모두 비회원 허용
		if ("GET".equalsIgnoreCase(method)
			&& pathMatcher.match("/owner-boards/**", url)) {

			debugLog("GET /owner-boards 비회원 전용 경로입니다. 필터 우회: " + url);
			filterChain.doFilter(request, response);
			return;
		}

		// GET 매핑의 /places 하위 경로 모두 비회원 허용
		if ("GET".equalsIgnoreCase(method)
			&& pathMatcher.match("/places/**", url)) {

			debugLog("GET /places 비회원 전용 경로입니다. 필터 우회: " + url);
			filterChain.doFilter(request, response);
			return;
		}

		// GET 매핑의 /trade-boards, /trade-boards/{tradeBoardId} 경로 비회원 허용
		if ("GET".equalsIgnoreCase(method)
			&& (pathMatcher.match("/trade-boards", url)
			|| pathMatcher.match("/trade-boards/*", url))) {

			debugLog("GET /trade-boards 비회원 전용 경로입니다. 필터 우회: " + url);
			filterChain.doFilter(request, response);
			return;
		}

		// GET 매핑의 /questions/today 경로 비회원 허용
		if ("GET".equalsIgnoreCase(method)
			&& pathMatcher.match("/questions/today", url)) {

			debugLog("GET /questions/today 비회원 전용 경로입니다. 필터 우회: " + url);
			filterChain.doFilter(request, response);
			return;
		}

		// GET 매핑의 /albums/all, /albums/all/users/{userId}, /albums/{albumId} 경로 비회원 허용
		if ("GET".equalsIgnoreCase(method)
			&& (url.equals("/albums/all")
			|| url.matches("^/albums/all/users/\\d+$")
			|| (url.matches("^/albums/\\d+$") && request.getHeader(HEADER_AUTHORIZATION) == null))) {

			debugLog("GET /albums 비회원 전용 경로입니다. 필터 우회: " + url);
			filterChain.doFilter(request, response);
			return;
		}

		// 웹소켓 요청 우회
		if (isWebSocketRequest(url)) {

			debugLog("WebSocket 연결 요청. 필터 우회: " + url);
			filterChain.doFilter(request, response);
			return;
		}

		// POST 매핑의 /test 하위 경로 모두 비회원 허용
		if ("POST".equalsIgnoreCase(method)
			&& pathMatcher.match("/test/**", url)) {

			debugLog("POST /test 비회원 전용 경로입니다. 필터 우회: " + url);
			filterChain.doFilter(request, response);
			return;
		}

		// 정적 리소스 우회
		if (url.matches(".*(\\.html|\\.css|\\.js|\\.png|\\.jpg|\\.ico)$")) {

			debugLog("정적 리소스 요청. 필터 우회: " + url);
			filterChain.doFilter(request, response);
			return;
		}

		// 1. Authorization 헤더 확인
		String bearerJwt = request.getHeader(HEADER_AUTHORIZATION);

		// 2. 쿼리 파라미터 accessToken 도 허용 (테스트 등)
		bearerJwt = getBearerJwt(request, bearerJwt);

		// 3. 최종적으로 bearerJwt 가 null 이면 인증 실패
		if (bearerJwt == null) {
			debugLog("Authorization 헤더 없음. 필터 중단");
			writeErrorResponse(response, HttpStatus.UNAUTHORIZED, "Authorization 헤더가 존재하지 않습니다.");
			return;
		}

		// 4. Bearer 형식 확인
		if (!bearerJwt.startsWith(TOKEN_PREFIX)) {
			writeErrorResponse(response, HttpStatus.UNAUTHORIZED, "유효한 Bearer 토큰이 없습니다.");
			return;
		}

		// 5. Bearer 제거 및 토큰 추출, 유효성 검사
		String rawToken;
		try {
			rawToken = jwtProvider.subStringToken(bearerJwt);
			debugLog("추출된 JWT: " + rawToken);
		} catch (CustomException e) {
			writeErrorResponse(response, e.getErrorCode().getStatus(), e.getMessage());
			return;
		}

		// 6. 블랙리스트(로그아웃) 토큰 체크
		if (loginRefreshToken.hasKey("BLACKLIST_" + rawToken)) {
			writeErrorResponse(response, HttpStatus.UNAUTHORIZED, "로그아웃된 토큰입니다.");
			return;
		}

		// 7. 토큰 파싱 및 인증정보 등록
		try {
			CustomPrincipal principal = extractPrincipalFromToken(rawToken);
			debugLog("JWT Claims 파싱 성공 - userId: " + principal.getId());

			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(principal,
				null, principal.getAuthorities());

			SecurityContextHolder.getContext().setAuthentication(authentication);
			debugLog("SecurityContext 등록 완료 - principal: " + principal.getEmail());

			debugLog("JWT 인증 완료. 다음 필터로 전달");
			filterChain.doFilter(request, response);

		} catch (CustomException e) {
			debugLog("JWT 검증 실패 - 이유: " + e.getMessage());
			writeErrorResponse(response, e.getErrorCode().getStatus(), e.getMessage());
		} catch (Exception e) {
			debugLog("검증 실패 - 이유: " + e.getMessage());
			writeErrorResponse(response, ErrorCode.FAILED_AUTHORIZATION.getStatus(), e.getMessage());
		}
	}

	/*
	 * 클라이언트가 요청한 JWT(Access Token)를 올바르게 추출하는 메서드
	 * 헤더(Authorization)가 비어 있을 경우, 쿼리 파라미터(accessToken)에서 토큰을 대체로 가져올 수 있도록 보완함
		→ Postman 등에서 Authorization 헤더를 생략(비회원) 하고 테스트하는 경우를 위해
	 */
	private String getBearerJwt(HttpServletRequest request, String bearerJwt) {

		// 클라이언트가 이미 Authorization 헤더에 JWT 담아 보낸 경우 그대로 반환
		if (bearerJwt != null) {
			return bearerJwt;
		}

		// Authorization 헤더가 없을 경우, 쿼리 파라미터의 accessToken 을 꺼냄
		String queryToken = request.getParameter("accessToken");
		// 쿼리 파라미너테도 없으면 JWT 못 얻기 때문에 null 반환 (인증 실패 처리)
		if (queryToken == null) {
			return null;
		}

		// 쿼리 파라미터에서 가져온 token 이 "Bearer "가 안 붙어있으면 붙여줌
		bearerJwt = formatAsBearer(queryToken);
		debugLog("accessToken 쿼리 파라미터 사용: " + bearerJwt);

		// 형식이 잘 갖춰지도록 보정된 JWT 반환
		return bearerJwt;
	}

	/*
	 * JWT 에서 Claims 정보를 추출하고, 인증된 사용자 정보(CustomPrincipal)를 객체로 변환하는 메서드
	 * rawToken(Bearer 접두사 제거된 상태) 에서 사용자 인증 정보 추출
	 * 사용자 인증 정보가 담긴 CustomPrincipal 객체로 변환해서 반환
	 */
	private CustomPrincipal extractPrincipalFromToken(String rawToken) {

		// 1. JWT 의 Claim 부분 파싱
		Claims claims = jwtProvider.parseRawToken(rawToken);

		// 2. 사용자 식별자(subject), 이메일, 닉네임 등 정보 추출
		Long userId = Long.valueOf(claims.getSubject());
		String email = claims.get("email", String.class);
		String nickname = claims.get("nickname", String.class);

		// 3. 권한(Role) 목록 추출 및 Spring 권한 객체로 변환
		List<String> roleList = jwtProvider.getRolesFromToken(rawToken);
		List<SimpleGrantedAuthority> authorities = roleList.stream()
			.map(SimpleGrantedAuthority::new)
			.toList();

		// 4. 사용자 정보를 담은 CustomPrincipal 반환
		return new CustomPrincipal(userId, email, nickname, authorities);
	}

	// 오류 응답 반환 (JSON)
	private void writeErrorResponse(HttpServletResponse response, HttpStatusCode status, String message)
		throws IOException {

		response.setStatus(status.value());
		response.setContentType("application/json;charset=UTF-8");
		response.getWriter().write(String.format("{\"status\":%d,\"message\":\"%s\"}", status.value(), message));
	}

	// 디버그 로그
	private void debugLog(String message) {

		log.debug("[JwtFilter] {}", message);
	}

	//웹소켓 경로 검증
	private boolean isWebSocketRequest(String url) {

		return pathMatcher.match("/ws-chat/**", url);
	}

	private String formatAsBearer(String token) {

		/*
		 * 표준 Authorization 형식으로 보정하기 위한 헬퍼 메서드
		 * token 이 이미 "Bearer "로 시작하면 그대로 반환
		 * 아닐 경우 "Bearer " 접두사 붙여서 반환
		 */
		return token.startsWith(TOKEN_PREFIX) ? token : TOKEN_PREFIX + token;
	}
}
