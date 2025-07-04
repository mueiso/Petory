package com.study.petory.domain.dailyqna.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.study.petory.domain.dailyqna.entity.DailyQuestion;
import com.study.petory.domain.dailyqna.entity.DailyQuestionStatus;

public interface DailyQuestionQueryRepository {

	boolean existsByDate(String date);

	Optional<DailyQuestion> findDailyQuestionByStatusAndId(List<DailyQuestionStatus> statusList, Long dailyQuestionId);

	Page<DailyQuestion> findDailyQuestionPageByStatus(List<DailyQuestionStatus> statusList, Pageable pageable);

	Optional<DailyQuestion> findTodayDailyQuestion(String date);

	Optional<DailyQuestionStatus> findDailyQuestionStatusById(Long dailyQuestionId);
}
