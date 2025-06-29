package com.study.petory.common.filter;

import java.util.Arrays;
import java.util.Optional;

public enum RateLimitType {
	REVIEW("reviews"),
	REPORT("reports");

	private final String uriSuffix;

	RateLimitType(String uriSuffix) {
		this.uriSuffix = uriSuffix;
	}

	public String getUriSuffix() {
		return "rate-limit:" + uriSuffix + ":";
	}

	public static Optional<RateLimitType> fromUri(String uri) {
		return Arrays.stream(values())
			.filter(type -> uri.endsWith("/" + type.uriSuffix))
			.findFirst();
	}
}
