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

		// 화이트리스트 경로 우회
		for (String pattern : securityWhitelist.getUrlWhitelist()) {

			if (pathMatcher.match(pattern, url)) {
				filterChain.doFilter(request, response);
				return;
			}
		}

		// GET 매핑의 /owner-boards 하위 경로 모두 비회원 허용
		if ("GET".equalsIgnoreCase(method)
			&& pathMatcher.match("/owner-boards/**", url)) {

			filterChain.doFilter(request, response);
			return;
		}

		if ("GET".equalsIgnoreCase(method) && pathMatcher.match("/places/rank", url)) {
			filterChain.doFilter(request, response);
			return;
		}

		// GET 매핑의 /places 하위 경로 모두 비회원 허용
		if ("GET".equalsIgnoreCase(method)
			&& pathMatcher.match("/places/**", url)) {

			filterChain.doFilter(request, response);
			return;
		}

		// GET 매핑의 /trade-boards, /trade-boards/{tradeBoardId} 경로 비회원 허용
		if ("GET".equalsIgnoreCase(method)
			&& (url.equals("/trade-boards")
			|| url.matches("^/trade-boards/\\d+$"))) {

			filterChain.doFilter(request, response);
			return;
		}

		// GET 매핑의 /questions/today 경로 비회원 허용
		if ("GET".equalsIgnoreCase(method)
			&& pathMatcher.match("/daily-questions/today", url)) {

			filterChain.doFilter(request, response);
			return;
		}

		// GET 매핑의 /albums/all, /albums/all/users/{userId}, /albums/{albumId} 경로 비회원 허용
		if ("GET".equalsIgnoreCase(method)
			&& (url.equals("/users/albums")
			|| url.matches("^/users/\\d+$/albums")
			|| (url.matches("^/users/albums/\\d+$") && request.getHeader(HEADER_AUTHORIZATION) == null))) {

			filterChain.doFilter(request, response);
			return;
		}

		// 웹소켓 요청 우회
		if (isWebSocketRequest(url)) {

			filterChain.doFilter(request, response);
			return;
		}

		// POST 매핑의 /test 하위 경로 모두 비회원 허용
		if ("POST".equalsIgnoreCase(method)
			&& pathMatcher.match("/test/**", url)) {

			filterChain.doFilter(request, response);
			return;
		}

		// 정적 리소스 우회
		if (url.matches(".*(\\.html|\\.css|\\.js|\\.png|\\.jpg|\\.ico)$")) {

			filterChain.doFilter(request, response);
			return;
		}

		// 1. Authorization 헤더 확인
		String bearerJwt = request.getHeader(HEADER_AUTHORIZATION);

		// 2. 쿼리 파라미터 accessToken 도 허용 (테스트 등)
		bearerJwt = getBearerJwt(request, bearerJwt);

		// 3. 최종적으로 bearerJwt 가 null 이면 인증 실패
		if (bearerJwt == null) {
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

			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(principal,
				null, principal.getAuthorities());

			SecurityContextHolder.getContext().setAuthentication(authentication);

			filterChain.doFilter(request, response);

		} catch (CustomException e) {
			writeErrorResponse(response, e.getErrorCode().getStatus(), e.getMessage());
		} catch (Exception e) {
			writeErrorResponse(response, ErrorCode.FAILED_AUTHORIZATION.getStatus(), e.getMessage());
		}
	}

	// 클라이언트가 요청한 JWT(Access Token)를 올바르게 추출하는 메서드
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

		// 형식이 잘 갖춰지도록 보정된 JWT 반환
		return bearerJwt;
	}

	// JWT 에서 Claims 정보를 추출하고, 인증된 사용자 정보(CustomPrincipal)를 객체로 변환하는 메서드
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

	//웹소켓 경로 검증
	private boolean isWebSocketRequest(String url) {

		return pathMatcher.match("/ws-chat/**", url);
	}

	private String formatAsBearer(String token) {

		return token.startsWith(TOKEN_PREFIX) ? token : TOKEN_PREFIX + token;
	}
}
