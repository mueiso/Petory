package com.study.petory.domain.dailyQna.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.study.petory.domain.dailyQna.entity.Question;

public interface QuestionRepository extends JpaRepository<Question, Long>, QuestionCustomRepository {
}
