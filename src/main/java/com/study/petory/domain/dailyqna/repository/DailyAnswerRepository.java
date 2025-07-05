package com.study.petory.domain.dailyqna.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.study.petory.domain.dailyqna.entity.DailyAnswer;

public interface DailyAnswerRepository extends JpaRepository<DailyAnswer, Long>, DailyAnswerQueryRepository {
}
