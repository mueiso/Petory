package com.study.petory.common.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

import net.fortuna.ical4j.model.DateTime;

import com.study.petory.common.exception.CustomException;
import com.study.petory.common.exception.enums.ErrorCode;

public class CustomDateUtil {
	public static final String FORMAT_DATE = "MM-dd";

	public static String getFormatDate() {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern(FORMAT_DATE));
	}

	public static Duration remainderTime() {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime lastTime = now.toLocalDate().plusDays(1).atStartOfDay().minusNanos(1);
		return Duration.between(now, lastTime);
	}

	public static LocalDateTime stringToLocalDateTime(String date) {
		// 데이터가 null이거나 앞 뒤에 불필요한 입력을 제거할 때 비어있다면 null 반환
		if (date == null || date.trim().isEmpty()) {
			return null;
		}

		// 불필요한 문자열을 제거하여 형식 맞추기
		String trimDate = date.trim();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

		try {
			// 타임존이 없는 경우
			if (trimDate.length() == 19) {
				return LocalDateTime.parse(trimDate, formatter);
			}

			// 타임존이 있는 경우
			if (trimDate.length() > 19) {
				// 공백인 경우 표준 ISO 형식으로 변환
				String input = trimDate.replace(" ", "+");
				// 표준 형식인 경우
				return ZonedDateTime.parse(input).toLocalDateTime();
			}
			// 19자 미만은 잘못된 형식
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

	public static LocalDateTime toISODateTime(String date) {
		DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
		return LocalDateTime.parse(date, formatter);
	}

	public static LocalDateTime stringToUTC(String datetime) {
		if (datetime == null) {
			return null;
		}
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmssX");
			OffsetDateTime offsetDateTime = OffsetDateTime.parse(datetime, formatter);
			return offsetDateTime.toLocalDateTime();
		} catch (Exception e) {
			throw new CustomException(ErrorCode.DATE_TIME_PARSE_FAIL);
		}
	}

}
