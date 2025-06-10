package com.study.petory.common.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {
	public static final String DATE_PATTERN = "yyyy-MM-dd";
	public static final String FORMAT_DATE = "MM-dd";

	public static String getFormatDate() {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern(FORMAT_DATE));
	}

	public static String getToday() {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_PATTERN));
	}
}
