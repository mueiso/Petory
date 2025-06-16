package com.study.petory.common.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {
	public static final String DATE_PATTERN = "yyyy-MM-dd";
	public static final String FORMAT_DATE = "MM-dd";

	public static String getFormatDate() {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern(FORMAT_DATE));
	}

	public static Duration remainderTime() {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime lastTime = now.toLocalDate().plusDays(1).atStartOfDay().minusNanos(1);
		return Duration.between(now, lastTime);
	}
}
