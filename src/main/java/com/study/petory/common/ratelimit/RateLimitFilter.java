package com.study.petory.common.ratelimit;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Supplier;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.petory.common.exception.enums.ErrorCode;
import com.study.petory.common.response.CommonResponse;
import com.study.petory.common.security.CustomPrincipal;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimitFilter extends OncePerRequestFilter {

	private final ProxyManager<String> proxyManager;
	private final Supplier<BucketConfiguration> bucketConfigurationSupplier;
	private final ObjectMapper objectMapper;

	@Override
	protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
		FilterChain filterChain) throws
		IOException,
		ServletException {

		String uri = httpServletRequest.getRequestURI();

		Optional<RateLimitType> rateLimitType = RateLimitType.fromUri(uri);
		if (rateLimitType.isEmpty()) {
			filterChain.doFilter(httpServletRequest, httpServletResponse);
			return;
		}
		String keyPrefix = rateLimitType.get().getUriSuffix();

		Long userId = extractUserId();
		if (userId == null) {
			httpServletResponse.sendError(HttpStatus.UNAUTHORIZED.value(), "로그인이 필요합니다.");
			return;
		}

		String key = keyPrefix + userId;

		Bucket bucket = proxyManager.builder().build(key, bucketConfigurationSupplier);

		if (bucket.tryConsume(1)) {
			log.info("✔️ 요청 허용 - 남은 토큰 : {}, (key : {})", bucket.getAvailableTokens(), key);
			filterChain.doFilter(httpServletRequest, httpServletResponse);
		} else {
			log.info("❌ 요청 차단 - 남은 토큰 : {}, (key : {})", bucket.getAvailableTokens(), key);
			CommonResponse<Object> errorResponse = new CommonResponse<>(ErrorCode.TOO_MANY_REQUESTS, null);

			httpServletResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value()); // ResponseEntity로 감싸지 않았기 때문에 필요
			httpServletResponse.setCharacterEncoding("UTF-8"); // 한글 깨짐 방지
			httpServletResponse.setContentType("application/json; charset=UTF-8");
			httpServletResponse.getWriter().write(objectMapper.writeValueAsString(errorResponse));
		}
	}

	private Long extractUserId() {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.getPrincipal() instanceof CustomPrincipal currentUser) {
			return currentUser.getId();
		}

		return null;
	}
}
