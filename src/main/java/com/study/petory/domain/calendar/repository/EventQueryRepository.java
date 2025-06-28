package com.study.petory.domain.calendar.repository;

import java.time.LocalDateTime;
import java.util.List;

import com.study.petory.domain.calendar.entity.Event;

public interface EventQueryRepository {
	// List<Event> findEventList(Long userId, LocalDateTime start, LocalDateTime end);
	List<Event> findEventListStart(Long userId, LocalDateTime start, LocalDateTime end);
}
