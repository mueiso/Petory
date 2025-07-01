package com.study.petory.domain.dailyQna.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.study.petory.domain.dailyQna.entity.DailyAnswer;

public interface DailyAnswerRepository extends JpaRepository<DailyAnswer, Long>, DailyAnswerQueryRepository {
}
