package com.study.petory.domain.dailyQna.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.study.petory.domain.dailyQna.entity.DailyQna;

public interface DailyQnaRepository extends JpaRepository<DailyQna, Long> {

	List<DailyQna> findByUserId_IdAndQuestionId_Id(Long userIdId, Long questionIdId);
}
