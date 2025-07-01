package com.study.petory.domain.dailyQna.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.study.petory.domain.dailyQna.entity.DailyQuestion;

public interface DailyQuestionRepository extends JpaRepository<DailyQuestion, Long>, DailyQuestionQueryRepository {
}
