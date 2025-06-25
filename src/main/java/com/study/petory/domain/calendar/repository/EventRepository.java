package com.study.petory.domain.calendar.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.study.petory.domain.calendar.entity.Event;

public interface EventRepository extends JpaRepository<Event, Long>, EventQueryRepository {
}
