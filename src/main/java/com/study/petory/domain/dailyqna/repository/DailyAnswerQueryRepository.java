package com.study.petory.domain.dailyqna.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.study.petory.domain.dailyqna.entity.DailyAnswer;
import com.study.petory.domain.dailyqna.entity.DailyAnswerStatus;

public interface DailyAnswerQueryRepository {

	boolean isDailyAnswerToday(Long userId, Long dailyQuestionId);

	Optional<DailyAnswer> findDailyAnswerByStatusAndId(List<DailyAnswerStatus> statusList, Long dailyAnswerId);

	List<DailyAnswer> findDailyAnswer(Long userId, Long dailyQuestionId);

	Page<DailyAnswer> findDailyAnswerPageByStatus(List<DailyAnswerStatus> statusList, Long userId, Pageable pageable);

	Optional<DailyAnswerStatus> findDailyAnswerStatusById(Long dailyAnswerId);
}
