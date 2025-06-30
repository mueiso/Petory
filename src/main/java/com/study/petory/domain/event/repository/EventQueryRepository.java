package com.study.petory.domain.event.repository;

import java.time.LocalDateTime;
import java.util.List;

import com.study.petory.domain.event.entity.Event;

public interface EventQueryRepository {
	List<Event> findEventListStart(Long userId, LocalDateTime start, LocalDateTime end);
}
