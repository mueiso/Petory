package com.study.petory.domain.event.entity;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import net.fortuna.ical4j.model.DateList;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.PeriodList;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.ExDate;
import net.fortuna.ical4j.model.property.RDate;
import net.fortuna.ical4j.model.property.RRule;

import com.study.petory.common.exception.CustomException;
import com.study.petory.common.exception.enums.ErrorCode;
import com.study.petory.common.util.CustomDateUtil;

@Component
public class Recurrence {

	private static final String RRULE = "RRULE";
	private static final String RDATE = "RDATE";
	private static final String EXDATE = "EXDATE";

	// 반복 일정의 시작일 List 생성
	public List<LocalDateTime> getInstanceStartTimeList(Event event, LocalDateTime start, LocalDateTime end) {
		VEvent vEvent = createVEvent(event);

		DateTime periodStart = new DateTime(CustomDateUtil.toDateTime(start));
		DateTime periodEnd = new DateTime(CustomDateUtil.toDateTime(end));
		Period period = new Period(periodStart, periodEnd);

		PeriodList instanceTimeList = vEvent.calculateRecurrenceSet(period);

		return instanceTimeList.stream()
			.map(time -> CustomDateUtil.toLocalDateTime(time.getStart()))
			.collect(Collectors.toList());
	}

	// VEvent 생성
	private VEvent createVEvent(Event event) {
		DateTime startDate = new DateTime(CustomDateUtil.toDateTime(event.getStartDate()));
		DateTime endDate = new DateTime(CustomDateUtil.toDateTime(event.getEndDate()));

		VEvent vEvent = new VEvent(startDate, endDate, event.getTitle());

		// RRULE 추가
		if (!event.isRruleBlank()) {
			try {
				RRule rRule = new RRule(event.getRrule());
				vEvent.getProperties().add(rRule);
			} catch (ParseException e) {
				throw new CustomException(ErrorCode.DATE_TIME_PARSE_FAIL);
			}
		}

		// RDATE 추가
		if (!event.isRDateBlank()) {
			DateList dateList = getDateList(event, RDATE);
			vEvent.getProperties().add(new RDate(dateList));
		}

		// EXDATE 추가
		if (!event.isExDateBlank()) {
			DateList dateList = getDateList(event, EXDATE);
			vEvent.getProperties().add(new ExDate(dateList));
		}
		return vEvent;
	}

	// 하나의 문자열로 합쳐져 있는 날짜들 분리
	public List<String> toListFromString(String date) {
		if (date == null || date.isBlank()) {
			return new ArrayList<>();
		}
		String[] responseList = date.split(",");
		return new ArrayList<>(Arrays.asList(responseList));
	}

	// 반복 조건 List에서 타입에 맞는 반복 조건 조회
	public String getRecurrence(String type, List<String> recurrence) {
		if (recurrence == null) {
			return null;
		}
		return recurrence.stream()
			// 원하는 type + : 조회
			.filter(i -> i.contains(type + ":"))
			// 해당 type 제거
			.map(i -> i.substring(type.length() + 1))
			.findFirst()
			.orElse(null);
	}

	// 반복 조건 List에서 반복 종료일 조회
	public LocalDateTime getRecurrenceEnd(List<String> recurrence) {
		String rrule = getRecurrence(RRULE, recurrence);
		if (rrule == null || !rrule.contains("UNTIL")) {
			return null;
		}
		String recurrenceEnd = rrule.replaceFirst(".*UNTIL=", "");
		return CustomDateUtil.stringToUTC(recurrenceEnd.substring(0, 16));
	}

	public DateList getDateList(Event event, String type) {
		List<String> dateTypeList = new ArrayList<>();
		DateList dateList = new DateList();

		if (type.equals(RDATE)) {
			dateTypeList = toListFromString(event.getRDate());
		}

		if (type.equals(EXDATE)) {
			dateTypeList = toListFromString(event.getExDate());
		}

		for (String rDate : dateTypeList) {
			try {
				LocalDateTime localDateTime = CustomDateUtil.toISODateTime(rDate);
				DateTime dateTime = new DateTime(CustomDateUtil.toDateTime(localDateTime));
				dateList.add(dateTime);
			} catch (RuntimeException e) {
				throw new CustomException(ErrorCode.DATE_TIME_PARSE_FAIL);
			}
		}
		return dateList;
	}

}
