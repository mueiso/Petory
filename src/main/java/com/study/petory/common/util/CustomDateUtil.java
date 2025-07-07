package com.study.petory.common.util;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

import net.fortuna.ical4j.model.DateTime;

import com.study.petory.common.exception.CustomException;
import com.study.petory.common.exception.enums.ErrorCode;

public class CustomDateUtil {
	private static final String FORMAT_DATE = "MM-dd";
	private static final DateTimeFormatter ISO_LOCAL_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
	private static final DateTimeFormatter DATE_ONLY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private static final DateTimeFormatter UTC_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmssX");

	public static String getFormatDate() {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern(FORMAT_DATE));
	}

	public static Duration remainderTime() {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime lastTime = now.toLocalDate().plusDays(1).atStartOfDay().minusNanos(1);
		return Duration.between(now, lastTime);
	}

	public static LocalDateTime stringToLocalDateTime(String date) {
		if (date == null || date.trim().isEmpty()) {
			return null;
		}

		String trimDate = date.trim();

		if (trimDate.isEmpty()) {
			return null;
		}
		int length = trimDate.length();

		try {
			// 타임존이 없는 경우
			if (length == 19) {
				return LocalDateTime.parse(trimDate, ISO_LOCAL_FORMATTER);
			}

			// 시간이 안 들어오는 경우
			if (trimDate.length() == 10) {
				return LocalDate.parse(trimDate, DATE_ONLY_FORMATTER).atStartOfDay();
			}

			// 타임존이 있는 경우
			if (length > 19) {
				if (trimDate.contains(" ")) {
					trimDate = trimDate.replaceAll(" ", "+");
				}
				return ZonedDateTime.parse(trimDate).toLocalDateTime();
			}

			throw new CustomException(ErrorCode.DATE_TIME_PARSE_FAIL);

		} catch (DateTimeParseException e) {
			throw new CustomException(ErrorCode.DATE_TIME_PARSE_FAIL);
		}
	}

	public static String toISOString(LocalDateTime localDateTime, String timeZone) {
		if (localDateTime == null) {
			return null;
		}
		ZoneId zone = ZoneId.of(timeZone);
		return localDateTime.atZone(zone).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
	}

	public static Date toDateTime(LocalDateTime localDateTime) {
		return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
	}

	public static LocalDateTime toLocalDateTime(DateTime dateTime) {
		return LocalDateTime.ofInstant(dateTime.toInstant(), ZoneId.systemDefault());
	}

	public static LocalDateTime stringToUTC(String datetime) {
		if (datetime == null && datetime.isEmpty()) {
			return null;
		}
		try {
			OffsetDateTime offsetDateTime = OffsetDateTime.parse(datetime, UTC_FORMATTER);
			return offsetDateTime.toLocalDateTime();
		} catch (Exception e) {
			throw new CustomException(ErrorCode.DATE_TIME_PARSE_FAIL);
		}
	}

	public static Long getDaysDifference(LocalDateTime start, LocalDateTime end) {
		if (end == null) {
			return null;
		}
		Period period = Period.between(start.toLocalDate(), end.toLocalDate());
		return (long)period.getDays();
	}
}
