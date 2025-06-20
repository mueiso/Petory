package com.study.petory.common.filter;

import static java.time.Duration.*;

import java.io.IOException;
import java.time.Duration;
import java.util.function.Supplier;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

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
	private final Supplier<BucketConfiguration> bucketConfigurationSupplier = createBucketConfig();

	@Override
	protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
		FilterChain filterChain) throws
		IOException,
		ServletException {

		String uri = httpServletRequest.getRequestURI();
		if (!uri.matches("/places/\\d+/reports")) {
			filterChain.doFilter(httpServletRequest, httpServletResponse);
			return;
		}

		Long userId = extractUserId(httpServletRequest);
		if (userId == null) {
			httpServletResponse.sendError(HttpStatus.UNAUTHORIZED.value(), "로그인이 필요합니다.");
			return;
		}

		String key = "rate-limit:report:" + userId;

		// 버킷 생성
		Bucket bucket = proxyManager.builder().build(key, bucketConfigurationSupplier);

		if (bucket.tryConsume(1)) {
			filterChain.doFilter(httpServletRequest, httpServletResponse);
		} else {
			httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			httpServletResponse.setCharacterEncoding("UTF-8");
			httpServletResponse.setContentType("text/plain; charset=UTF-8");
			httpServletResponse.getWriter().write("신고는 1시간에 최대 10회까지 가능합니다.");
		}
	}

	private Supplier<BucketConfiguration> createBucketConfig() {
		return () -> BucketConfiguration.builder()
			.addLimit(limit -> limit.capacity(10).refillGreedy(10, Duration.ofHours(1)))
			.build();
	}

	private Long extractUserId(HttpServletRequest httpServletRequest) {

		// 현재 로그인된 사용자의 인증 정보를 꺼내는 과정
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.getPrincipal() instanceof CustomPrincipal currentUser) {
			return currentUser.getId();
		}

		return null;
	}

	public Supplier<BucketConfiguration> getConfigSupplier() {
		return () ->
			BucketConfiguration.builder()
				.addLimit(limit -> limit.capacity(10).refillGreedy(10, ofMinutes(1)))
				.build();
	}
}
