package com.study.petory.domain.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.study.petory.domain.event.entity.Event;

public interface EventRepository extends JpaRepository<Event, Long>, EventQueryRepository {
}
