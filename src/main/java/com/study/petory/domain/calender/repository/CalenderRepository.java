package com.study.petory.domain.calender.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.study.petory.domain.calender.entity.Calender;

public interface CalenderRepository extends JpaRepository<Calender, Long> {
}
