package com.study.petory.domain.dailyQna.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.study.petory.domain.dailyQna.entity.DailyQna;

public interface DailyQnaRepository extends JpaRepository<DailyQna, Long>, DailyQnaCustomRepository {
}
