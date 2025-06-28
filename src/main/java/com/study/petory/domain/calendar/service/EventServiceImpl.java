package com.study.petory.domain.calendar.service;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.fortuna.ical4j.model.DateList;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.PeriodList;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.RRule;

import com.study.petory.common.exception.CustomException;
import com.study.petory.common.exception.enums.ErrorCode;
import com.study.petory.common.util.CustomDateUtil;
import com.study.petory.domain.calendar.dto.request.EventCreateRequestDto;
import com.study.petory.domain.calendar.dto.request.EventUpdateRequestDto;
import com.study.petory.domain.calendar.dto.response.EventCreateResponseDto;
import com.study.petory.domain.calendar.dto.response.EventGetOneResponseDto;
import com.study.petory.domain.calendar.dto.response.EventInstanceGetResponseDto;
import com.study.petory.domain.calendar.dto.response.EventUpdateResponseDto;
import com.study.petory.domain.calendar.entity.Event;
import com.study.petory.domain.calendar.repository.EventRepository;
import com.study.petory.domain.user.entity.User;
import com.study.petory.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

	private final EventRepository eventRepository;
	private final UserService userService;

	// 일정 조회
	@Override
	public Event findEventById(Long eventId) {
		return eventRepository.findById(eventId)
			.orElseThrow(() -> new CustomException(ErrorCode.CALENDER_NOT_FOUND));
	}

	// 이벤트 생성
	@Override
	@Transactional
	public EventCreateResponseDto saveEvent(Long userId, EventCreateRequestDto request) {
		User user = userService.getUserById(userId);

		Event event = Event.builder()
			.user(user)
			.title(request.getTitle())
			.startDate(CustomDateUtil.stringToLocalDateTime(request.getStartDate()))
			.endDate(CustomDateUtil.stringToLocalDateTime(request.getEndDate()))
			.timeZone(request.getTimeZone())
			.isAllDay(Optional.ofNullable(request.getIsAllDay()).orElse(true))
			.rrule(getRecurrence("RRULE", request.getRecurrence()))
			.recurrenceEnd(getRecurrenceEnd(request.getRecurrence()))
			.rDate(getRecurrence("RDATE", request.getRecurrence()))
			.exDate(getRecurrence("EXDATE", request.getRecurrence()))
			.description(request.getDescription())
			.color(request.getColor())
			.build();

		Event savedEvent = eventRepository.save(event);
		return EventCreateResponseDto.from(savedEvent);
	}

	// 일정 범위 조회
	@Override
	@Transactional
	public List<EventInstanceGetResponseDto> findEvents(Long userId, String start, String end) {
		LocalDateTime startDate = CustomDateUtil.stringToLocalDateTime(start);
		LocalDateTime endDate = CustomDateUtil.stringToLocalDateTime(end);

		List<Event> eventList = eventRepository.findEventListStart(userId, startDate, endDate);

		return eventList.stream()
			.flatMap(event -> {
				if (event.getRrule() == null) {
					return Stream.of(EventInstanceGetResponseDto.from(event, null));
				} else {
					List<LocalDateTime> instanceTimeList = getEventInstanceTimeList(event, startDate, endDate);
					return instanceTimeList.stream()
						.map(instanceTime -> EventInstanceGetResponseDto.from(event, instanceTime));
				}
			})
			.sorted(Comparator.comparing(EventInstanceGetResponseDto::getStartDate))
			.collect(Collectors.toList());
	}

	// 일정 단일 조회
	@Override
	public EventGetOneResponseDto findOneEvent(Long eventId) {
		Event event = findEventById(eventId);
		return EventGetOneResponseDto.from(event);
	}

	// 일정 수정
	@Override
	@Transactional
	public EventUpdateResponseDto updateEvent(Long userId, Long eventId, EventUpdateRequestDto request) {
		Event event = findEventById(eventId);
		validUser(userId, event);

		event.updateEvent(
			request.getTitle(),
			CustomDateUtil.stringToLocalDateTime(request.getStartDate()),
			CustomDateUtil.stringToLocalDateTime(request.getEndDate()),
			request.getTimeZone(),
			request.getIsAllDay(),
			getRecurrence("RRULE", request.getRecurrence()),
			getRecurrenceEnd(request.getRecurrence()),
			getRecurrence("RDATE", request.getRecurrence()),
			getRecurrence("EXDATE", request.getRecurrence()),
			request.getDescription(),
			request.getColor()
		);

		return EventUpdateResponseDto.from(event);
	}

	// 일정 삭제
	@Override
	@Transactional
	public void deleteEvent(Long userId, Long eventId) {
		Event event = findEventById(eventId);
		validUser(userId, event);
		eventRepository.deleteById(eventId);
	}

	// 작성자 검증
	public void validUser(Long userId, Event event) {
		if (!event.isEqualUser(userId)) {
			throw new CustomException(ErrorCode.ONLY_AUTHOR_CAN_EDIT);
		}
	}

	// 반복 일정 인스턴스 List 생성
	public List<LocalDateTime> getEventInstanceTimeList(Event event, LocalDateTime start, LocalDateTime end) {
		VEvent vEvent = createVEvent(event);

		DateTime periodStart = new DateTime(CustomDateUtil.toDateTime(start));
		DateTime periodEnd = new DateTime(CustomDateUtil.toDateTime(end));
		Period period = new Period(periodStart, periodEnd);

		PeriodList instanceTimeList = vEvent.calculateRecurrenceSet(period);

		return instanceTimeList.stream()
			.map(a -> CustomDateUtil.toLocalDateTime(a.getStart()))
			.sorted()
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
			List<String> rDateList = toListFromString(event.getRDate());
			DateList dateList = new DateList();
			for (String rDate : rDateList) {
				try {
					LocalDateTime localDateTime = CustomDateUtil.toISODateTime(rDate);
					DateTime dateTime = new DateTime(CustomDateUtil.toDateTime(localDateTime));
					dateList.add(dateTime);
				} catch (RuntimeException e) {
					throw new CustomException(ErrorCode.DATE_TIME_PARSE_FAIL);
				}
			}
		}

		// EXDATE 추가
		if (!event.isExDateBlank()) {
			List<String> exDateList = toListFromString(event.getExDate());
			DateList dateList = new DateList();
			for (String exDate : exDateList) {
				try {
					LocalDateTime localDateTime = CustomDateUtil.toISODateTime(exDate);
					DateTime dateTime = new DateTime(CustomDateUtil.toDateTime(localDateTime));
					dateList.add(dateTime);
				} catch (RuntimeException e) {
					throw new CustomException(ErrorCode.DATE_TIME_PARSE_FAIL);
				}
			}
		}
		return vEvent;
	}

	//
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
		String rrule = getRecurrence("RRULE", recurrence);
		if (rrule == null || !rrule.contains("UNTIL")) {
			return null;
		}
		String recurrenceEnd = rrule.replaceFirst(".*UNTIL=", "");
		return CustomDateUtil.stringToUTC(recurrenceEnd.substring(0, 16));
	}
}
