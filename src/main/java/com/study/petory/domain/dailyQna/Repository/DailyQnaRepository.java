package com.study.petory.domain.dailyQna.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.study.petory.domain.dailyQna.entity.DailyQna;
import com.study.petory.exception.CustomException;
import com.study.petory.exception.enums.ErrorCode;

public interface DailyQnaRepository extends JpaRepository<DailyQna, Long> {

	default DailyQna findByIdOrElseThrow(Long dailyQnaId) {
		return findById(dailyQnaId).orElseThrow(() -> new CustomException(ErrorCode.DAILY_QNA_NOT_FOUND));
	}

	List<DailyQna> findByUserId_IdAndQuestionId_Id(Long userIdId, Long questionIdId);
}
