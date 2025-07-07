package com.study.petory.domain.event.service;

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
import com.study.petory.domain.event.entity.Event;
import com.study.petory.domain.event.entity.RecurrenceInfo;

@Component
public class RecurrenceService {

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
		LocalDateTime end = event.getStartDate();
		if (event.getEndDate() != null) {
			end = event.getEndDate();
		}
		DateTime endDate = new DateTime(CustomDateUtil.toDateTime(end));


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
	public RecurrenceInfo getRecurrence(List<String> recurrence) {
		if (recurrence == null) {
			return null;
		}

		RecurrenceInfo info = new RecurrenceInfo();

		for (String rule : recurrence) {
			if (rule.startsWith("RRULE:")) {
				String rrule = rule.substring(6);
				info.setRrule(rrule);

				if (rrule.contains("UNTIL=")) {
					int until = rrule.indexOf("UNTIL=") + 6;
					if (rrule.length() - until >= 16) {
						String untilValue = rrule.substring(until, until + 16);
						info.setRecurrenceEnd(CustomDateUtil.stringToUTC(untilValue));
					}
				}
			} else if (rule.startsWith("RDATE:")) {
				info.setRDate(rule.substring(6));
			} else if (rule.startsWith("EXDATE:")) {
				info.setExDate(rule.substring(7));
			}
		}
		return info;
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
				LocalDateTime localDateTime = CustomDateUtil.stringToUTC(rDate);
				DateTime dateTime = new DateTime(CustomDateUtil.toDateTime(localDateTime));
				dateList.add(dateTime);
			} catch (RuntimeException e) {
				throw new CustomException(ErrorCode.DATE_TIME_PARSE_FAIL);
			}
		}
		return dateList;
	}
}
