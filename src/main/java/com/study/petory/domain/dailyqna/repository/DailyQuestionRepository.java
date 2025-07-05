package com.study.petory.domain.dailyqna.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.study.petory.domain.dailyqna.entity.DailyQuestion;

public interface DailyQuestionRepository extends JpaRepository<DailyQuestion, Long>, DailyQuestionQueryRepository {
}
